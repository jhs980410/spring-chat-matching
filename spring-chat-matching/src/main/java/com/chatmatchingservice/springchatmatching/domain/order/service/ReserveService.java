package com.chatmatchingservice.springchatmatching.domain.order.service;

import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
import com.chatmatchingservice.springchatmatching.domain.event.repository.EventRepository;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateRequestDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateResponseDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderSeatItemDto;
import com.chatmatchingservice.springchatmatching.domain.order.repository.TicketOrderItemRepository;
import com.chatmatchingservice.springchatmatching.domain.order.repository.TicketOrderRepository;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Ticket;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderItem;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.TicketRepository;
import com.chatmatchingservice.springchatmatching.domain.user.entity.AppUser;
import com.chatmatchingservice.springchatmatching.domain.user.repository.AppUserRepository;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.entity.ReserveUser;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.repository.ReserveUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
@Service
@RequiredArgsConstructor
@Transactional
public class ReserveService {

    private final TicketOrderRepository orderRepository;
    private final TicketOrderItemRepository orderItemRepository;
    private final SeatLockService seatLockService;

    private final AppUserRepository userRepository;
    private final ReserveUserRepository reserveUserRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository; // 🔥 추가

    public OrderCreateResponseDto createOrder(
            Long userId,
            OrderCreateRequestDto request
    ) {
        // 1️⃣ 좌석 락 검증
        Set<Long> lockedSeats =
                seatLockService.getUserLockedSeats(userId, request.eventId());

        for (OrderSeatItemDto item : request.items()) {
            if (!lockedSeats.contains(item.seatId())) {
                throw new IllegalStateException("좌석 락이 유효하지 않습니다.");
            }
        }

        // 2️⃣ 연관 엔티티 로딩
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        ReserveUser reserveUser = reserveUserRepository.findById(request.reserveUserId())
                .orElseThrow(() -> new IllegalArgumentException("예매자 없음"));

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new IllegalArgumentException("이벤트 없음"));

        // 3️⃣ 주문 생성
        TicketOrder order = TicketOrder.create(user, reserveUser, event);
        orderRepository.save(order);

        // 4️⃣ 주문 아이템 생성
        int totalPrice = 0;

        for (OrderSeatItemDto item : request.items()) {

            Ticket ticket = ticketRepository.findById(item.ticketId())
                    .orElseThrow(() -> new IllegalArgumentException("티켓 없음"));

            TicketOrderItem orderItem =
                    TicketOrderItem.create(order, ticket, item.unitPrice());

            orderItemRepository.save(orderItem);
            totalPrice += item.unitPrice();
        }

        // 5️⃣ 주문 상태 변경
        order.complete(totalPrice);
        orderRepository.save(order);

        return new OrderCreateResponseDto(
                order.getId(),
                totalPrice,
                order.getStatus().name(),
                order.getOrderedAt()
        );
    }
}
