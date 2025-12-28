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
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
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
    private final ReserveUserRepository reserveUserRepository;
    public OrderCreateResponseDto createOrder(
            Long userId,
            OrderCreateRequestDto request
    ) {
        // 유저 조회
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 이벤트 조회
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new IllegalArgumentException("이벤트 없음"));

        // 예매자 조회
        ReserveUser reserveUser = reserveUserRepository.findById(request.reserveUserId())
                .orElseThrow(() -> new IllegalArgumentException("예매자 없음"));

        // 주문 생성 (PENDING)
        TicketOrder order = TicketOrder.create(user, reserveUser, event);
        orderRepository.save(order);

        return new OrderCreateResponseDto(order.getId());
    }

    public void prepareReservation(
            Long orderId,
            Long eventId,
            List<Long> seatIds
    ) {
        // 1️⃣ 명시적 상태 체크
        String status =
                seatLockService.getReservationStatus(eventId, orderId);

        if ("IN_PROGRESS".equals(status)) {
            throw new IllegalStateException("이미 예매 진행 중입니다.");
        }

        // 2️⃣ 좌석 락
        SeatLockResultDto result =
                seatLockService.lockSeats(orderId, eventId, seatIds);

        if (!result.success()) {
            throw new IllegalStateException(result.message());
        }

        // 3️⃣ 상태 명시적 기록
        seatLockService.markInProgress(eventId, orderId);
    }
}
