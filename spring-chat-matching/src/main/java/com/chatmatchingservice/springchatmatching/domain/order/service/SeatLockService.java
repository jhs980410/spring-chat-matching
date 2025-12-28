package com.chatmatchingservice.springchatmatching.domain.order.service;

import com.chatmatchingservice.springchatmatching.domain.order.dto.SeatLockResultDto;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class SeatLockService {

    private static final long LOCK_TTL_SECONDS = 300;

    private final RedisRepository redisRepository;

    /**
     * 좌석 락 (order 기준)
     */
    public SeatLockResultDto lockSeats(
            Long orderId,
            Long eventId,
            List<Long> seatIds
    ) {

        List<Long> locked = new ArrayList<>();
        List<Long> failed = new ArrayList<>();

        for (Long seatId : seatIds) {
            boolean success = redisRepository.tryLockSeat(
                    eventId,
                    seatId,
                    orderId,
                    LOCK_TTL_SECONDS
            );

            if (success) {
                locked.add(seatId);
                redisRepository.addOrderLockedSeat(orderId, eventId, seatId);
            } else {
                failed.add(seatId);
            }
        }

        if (!failed.isEmpty()) {
            locked.forEach(seatId ->
                    redisRepository.unlockSeat(eventId, seatId)
            );
            redisRepository.clearOrderLockedSeats(orderId, eventId);

            return new SeatLockResultDto(
                    false,
                    List.of(),
                    failed,
                    "이미 선택된 좌석이 있습니다."
            );
        }

        redisRepository.setReservationStatus(eventId, orderId, "LOCKED");

        return new SeatLockResultDto(
                true,
                locked,
                List.of(),
                "좌석이 잠겼습니다."
        );
    }

    /**
     * 좌석 락 해제 (order 기준)
     */
    public void unlockSeats(Long orderId, Long eventId) {

        Set<Long> seats =
                redisRepository.getOrderLockedSeats(orderId, eventId);

        for (Long seatId : seats) {
            redisRepository.unlockSeat(eventId, seatId);
        }

        redisRepository.clearOrderLockedSeats(orderId, eventId);
        redisRepository.clearReservationStatus(eventId, orderId);
    }

    public Set<Long> getOrderLockedSeats(Long orderId, Long eventId) {
        return redisRepository.getOrderLockedSeats(orderId, eventId);
    }

    public void markInProgress(Long eventId, Long orderId) {
        redisRepository.setReservationStatus(eventId, orderId, "IN_PROGRESS");
    }

    public String getReservationStatus(Long eventId, Long orderId) {
        return redisRepository.getReservationStatus(eventId, orderId);
    }
}
