package com.chatmatchingservice.springchatmatching.domain.payment.service;

import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateRequestDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateResponseDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.SeatLockResultDto;
import com.chatmatchingservice.springchatmatching.domain.order.service.SeatLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final RedissonClient redissonClient;
    private final OrderCommandService orderCommandService;
    private final SeatLockService seatLockService;

    public OrderCreateResponseDto createOrder(Long userId, OrderCreateRequestDto request) {
        // 1. 요청 검증
        if (request.seatIds() == null || request.seatIds().isEmpty()) {
            throw new IllegalArgumentException("좌석이 선택되지 않았습니다.");
        }

        // 2. 멀티락 키 생성
        List<RLock> locks = request.seatIds().stream()
                .map(seatId -> redissonClient.getLock("lock:event:" + request.eventId() + ":seat:" + seatId))
                .toList();

        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));

        try {
            // 3. 락 획득 시도 (대기 시간을 10초로 늘려 안정성 확보)
            // waitTime: 10초 (부하 상황을 대비해 조금 더 기다림)
            // leaseTime: 5초 (비즈니스 로직이 충분히 끝날 시간, 너무 길면 장애 시 락이 오래 묶임)
            boolean available = multiLock.tryLock(10, 5, TimeUnit.SECONDS);

            if (!available) {
                log.warn("좌석 선점 실패 - 유저ID: {}, 좌석: {}", userId, request.seatIds());
                throw new IllegalStateException("이미 선택된 좌석이 포함되어 있습니다.");
            }

            // 4. 락 획득 성공 후 실제 DB 트랜잭션 실행
            log.info("좌석 락 획득 성공 - 유저ID: {}, 주문 생성 시작", userId);
            return orderCommandService.saveOrderWithTransaction(userId, request);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("잠금 획득 중 오류가 발생했습니다.");
        } finally {
            // 5. 안전한 락 해제 (UnsupportedOperationException 방지)
            try {
                // isHeldByCurrentThread() 대신 isLocked()를 사용하거나
                // 해당 스레드가 락을 가지고 있는지 여부와 상관없이 unlock을 시도하되 예외를 잡아줍니다.
                if (multiLock != null && multiLock.isLocked()) {
                    multiLock.unlock();
                    log.info("좌석 락 해제 완료 - 유저ID: {}", userId);
                }
            } catch (IllegalMonitorStateException e) {
                // 이미 타임아웃으로 해제되었거나 락을 소유하고 있지 않은 경우 발생
                log.debug("이미 해제된 락입니다 - 유저ID: {}", userId);
            } catch (Exception e) {
                log.error("락 해제 중 예상치 못한 에러 발생: {}", e.getMessage());
            }
        }
    }

    public void prepareReservation(Long orderId, Long eventId, List<Long> seatIds) {
        String status = seatLockService.getReservationStatus(eventId, orderId);

        if ("IN_PROGRESS".equals(status)) {
            throw new IllegalStateException("이미 예매 진행 중입니다.");
        }

        SeatLockResultDto result = seatLockService.lockSeats(orderId, eventId, seatIds);

        if (!result.success()) {
            throw new IllegalStateException(result.message());
        }

        seatLockService.markInProgress(eventId, orderId);
    }
}