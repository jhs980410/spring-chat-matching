package com.chatmatchingservice.springchatmatching.domain.payment.service;

import com.chatmatchingservice.springchatmatching.domain.ReserveUser.entity.ReserveUser;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.repository.ReserveUserRepository;
import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
import com.chatmatchingservice.springchatmatching.domain.event.repository.EventRepository;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateRequestDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateResponseDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.SeatLockResultDto;
import com.chatmatchingservice.springchatmatching.domain.order.repository.TicketOrderRepository;
import com.chatmatchingservice.springchatmatching.domain.order.service.SeatLockService;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Seat;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderItem;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.SeatRepository;
import com.chatmatchingservice.springchatmatching.domain.user.entity.AppUser;
import com.chatmatchingservice.springchatmatching.domain.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final SeatLockService seatLockService;
    private final TicketOrderRepository orderRepository;
    private final AppUserRepository userRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public OrderCreateResponseDto createOrder(
            Long userId,
            OrderCreateRequestDto request
    ) {
        if (request.seatIds() == null || request.seatIds().isEmpty()) {
            throw new IllegalArgumentException("좌석이 선택되지 않았습니다.");
        }

        if (request.seatIds().stream().anyMatch(id -> id == null)) {
            throw new IllegalArgumentException("좌석 ID에 null이 포함되어 있습니다.");
        }
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new IllegalArgumentException("이벤트 없음"));

        // 1️⃣ 주문 생성 (PENDING)
        TicketOrder order = TicketOrder.create(user, event);

        // 2️⃣ 좌석 → 주문 아이템 생성
        List<Seat> seats = seatRepository.findAllById(request.seatIds());
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("선택된 좌석이 없습니다.");
        }

        for (Seat seat : seats) {
            TicketOrderItem item =
                    TicketOrderItem.create(seat, seat.getPrice());
            order.addItem(item);
        }

        // 3️⃣ 금액 확정 (🔥 핵심)
        order.confirmOrder();

        orderRepository.save(order);
        return new OrderCreateResponseDto(order.getId());
    }

    public void prepareReservation(
            Long orderId,
            Long eventId,
            List<Long> seatIds
    ) {
        String status =
                seatLockService.getReservationStatus(eventId, orderId);

        if ("IN_PROGRESS".equals(status)) {
            throw new IllegalStateException("이미 예매 진행 중입니다.");
        }

        SeatLockResultDto result =
                seatLockService.lockSeats(orderId, eventId, seatIds);

        if (!result.success()) {
            throw new IllegalStateException(result.message());
        }

        seatLockService.markInProgress(eventId, orderId);
    }
}
