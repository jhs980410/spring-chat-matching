package com.chatmatchingservice.springchatmatching.domain.payment.service;

import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
import com.chatmatchingservice.springchatmatching.domain.event.repository.EventRepository;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateRequestDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateResponseDto;
import com.chatmatchingservice.springchatmatching.domain.order.repository.TicketOrderRepository;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Seat;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderItem;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.SeatRepository;
import com.chatmatchingservice.springchatmatching.domain.user.entity.AppUser;
import com.chatmatchingservice.springchatmatching.domain.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final TicketOrderRepository orderRepository;
    private final AppUserRepository userRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public OrderCreateResponseDto saveOrderWithTransaction(Long userId, OrderCreateRequestDto request) {
        // 1. 기초 정보 조회
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다."));

        // 2. 좌석 조회 및 상태 검증
        List<Seat> seats = seatRepository.findAllById(request.seatIds());
        if (seats.size() != request.seatIds().size()) {
            throw new IllegalArgumentException("선택한 좌석 중 존재하지 않는 좌석이 포함되어 있습니다.");
        }

        // 3. [중요] 좌석 점유 처리 (Double-Check)
        // 분산 락을 통과했더라도 DB 상에서 실제 예약 여부를 한 번 더 확인합니다.
        for (Seat seat : seats) {
            if (seat.isReserved()) {
                log.warn("이미 예약된 좌석 중복 시도 방어 - 좌석번호: {}, 유저ID: {}", seat.getSeatNumber(), userId);
                throw new IllegalStateException("이미 예약된 좌석이 포함되어 있습니다: " + seat.getSeatNumber());
            }
            // Seat 엔티티의 isReserved를 true로 변경
            seat.reserve();
        }

        // 4. 주문 및 주문 항목 생성
        TicketOrder order = TicketOrder.create(user, event);
        for (Seat seat : seats) {
            order.addItem(TicketOrderItem.create(seat));
        }

        // 5. 주문 확정 및 저장
        order.confirmOrder();
        orderRepository.save(order);

        log.info("주문 생성 성공 - 주문ID: {}, 유저ID: {}, 예약 좌석: {}", order.getId(), userId, request.seatIds());
        return new OrderCreateResponseDto(order.getId());
    }
}