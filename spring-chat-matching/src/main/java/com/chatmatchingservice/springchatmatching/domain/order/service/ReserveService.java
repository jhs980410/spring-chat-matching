//package com.chatmatchingservice.springchatmatching.domain.order.service;
//
//import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
//import com.chatmatchingservice.springchatmatching.domain.event.repository.EventRepository;
//import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateRequestDto;
//import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateResponseDto;
//import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderSeatItemDto;
//import com.chatmatchingservice.springchatmatching.domain.order.repository.TicketOrderItemRepository;
//import com.chatmatchingservice.springchatmatching.domain.order.repository.TicketOrderRepository;
//import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Ticket;
//import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
//import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderItem;
//import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderStatus;
//import com.chatmatchingservice.springchatmatching.domain.ticket.repository.TicketRepository;
//import com.chatmatchingservice.springchatmatching.domain.user.entity.AppUser;
//import com.chatmatchingservice.springchatmatching.domain.user.repository.AppUserRepository;
//import com.chatmatchingservice.springchatmatching.domain.ReserveUser.entity.ReserveUser;
//import com.chatmatchingservice.springchatmatching.domain.ReserveUser.repository.ReserveUserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Set;
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class ReserveService {
//
//    private final TicketOrderRepository orderRepository;
//    private final TicketOrderItemRepository orderItemRepository;
//    private final SeatLockService seatLockService;
//    private final TicketRepository ticketRepository;
//
//    public OrderCreateResponseDto confirmOrderItems(
//            Long orderId,
//            OrderCreateRequestDto request
//    ) {
//        // 1️⃣ 주문 조회
//        TicketOrder order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));
//
//        // 🔥 ORDERED / PAID 모두 차단
//        if (order.getStatus() != TicketOrderStatus.PENDING) {
//            throw new IllegalStateException("이미 처리된 주문입니다.");
//        }
//
//        // 2️⃣ 좌석 락 검증 (orderId 기준)
//        Set<Long> lockedSeats =
//                seatLockService.getOrderLockedSeats(orderId, request.eventId());
//
//        for (OrderSeatItemDto item : request.items()) {
//            if (!lockedSeats.contains(item.seatId())) {
//                throw new IllegalStateException("좌석 락이 유효하지 않습니다.");
//            }
//        }
//
//        // 3️⃣ 주문 아이템 구성
//        for (OrderSeatItemDto item : request.items()) {
//            Ticket ticket = ticketRepository.findById(item.ticketId())
//                    .orElseThrow(() -> new IllegalArgumentException("티켓 없음"));
//
//            TicketOrderItem orderItem =
//                    TicketOrderItem.create(order, ticket, item.unitPrice());
//
//            orderItemRepository.save(orderItem);
//        }
//
//        // 4️⃣ 금액 확정 (ORDERED, 결제 대기 상태)
//        order.confirmOrder();
//
//        return new OrderCreateResponseDto(
//                order.getId(),
//                order.getTotalPrice(),
//                order.getStatus().name(),
//                order.getOrderedAt()
//        );
//    }
//}
