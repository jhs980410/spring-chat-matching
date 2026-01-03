package com.chatmatchingservice.springchatmatching.domain.payment.controller;

import com.chatmatchingservice.springchatmatching.domain.event.service.WaitingRoomService; // 추가
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateRequestDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateResponseDto;
import com.chatmatchingservice.springchatmatching.domain.payment.service.ReservationService;
import com.chatmatchingservice.springchatmatching.domain.ticket.dto.SeatReserveRequest;
import com.chatmatchingservice.springchatmatching.global.error.CustomException; // 추가
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode; // 추가
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Reservation / Order",
        description = "티켓 예매 및 좌석 선점 API"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class ReservationController {

    private final ReservationService reservationService;
    private final WaitingRoomService waitingRoomService; // 1. 주입 추가

    // ========================================
    // 주문 생성 (PENDING)
    // ========================================
    @Operation(summary = "예매 주문 생성")
    @PostMapping
    public OrderCreateResponseDto createOrder(
            @RequestBody OrderCreateRequestDto request,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();

        // 2. 메서드 첫 줄에서 입장권 검증
        if (!waitingRoomService.canAccess(request.eventId(), userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return reservationService.createOrder(userId, request);
    }

    // ========================================
    // 좌석 선점 (Redis Lock)
    // ========================================
    @Operation(summary = "좌석 선점")
    @PostMapping("/{orderId}/reserve")
    public void reserve(
            @PathVariable Long orderId,
            @RequestBody SeatReserveRequest request,
            Authentication auth // 인증 정보 추가
    ) {
        Long userId = (Long) auth.getPrincipal();

        // 3. 메서드 첫 줄에서 입장권 검증
        if (!waitingRoomService.canAccess(request.eventId(), userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        reservationService.prepareReservation(
                orderId,
                request.eventId(),
                request.seatIds()
        );
    }
}