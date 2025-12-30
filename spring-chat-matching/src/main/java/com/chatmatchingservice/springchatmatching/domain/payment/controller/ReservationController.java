package com.chatmatchingservice.springchatmatching.domain.payment.controller;

import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateRequestDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateResponseDto;
import com.chatmatchingservice.springchatmatching.domain.payment.service.ReservationService;
import com.chatmatchingservice.springchatmatching.domain.ticket.dto.SeatReserveRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Reservation / Order",
        description = """
    티켓 예매 및 좌석 선점 API

    - 예매 주문 생성 (PENDING)
    - 좌석 Redis Lock 기반 선점 처리
    - 결제 전 단계의 예약 흐름 관리
    """
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class ReservationController {

    private final ReservationService reservationService;

    // ========================================
    // 주문 생성 (PENDING)
    // ========================================
    @Operation(
            summary = "예매 주문 생성",
            description = """
        티켓 예매를 위한 주문을 생성하는 API

        - 주문 상태를 PENDING으로 생성
        - 사용자 기준 단일 활성 주문 보장
        - 결제 및 좌석 선점의 시작 지점

        ※ 부하 테스트 핵심 트래픽 API
        """
    )
    @PostMapping
    public OrderCreateResponseDto createOrder(
            @RequestBody OrderCreateRequestDto request,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();
        return reservationService.createOrder(userId, request);
    }

    // ========================================
    // 좌석 선점 (Redis Lock)
    // ========================================
    @Operation(
            summary = "좌석 선점",
            description = """
        선택한 좌석을 Redis Lock으로 선점하는 API

        - orderId 기준 좌석 선점
        - Redis SET NX + TTL 기반 Lock
        - 결제 성공 시 좌석 확정, 실패 시 자동 해제
        """
    )
    @PostMapping("/{orderId}/reserve")
    public void reserve(
            @PathVariable Long orderId,
            @RequestBody SeatReserveRequest request
    ) {
        reservationService.prepareReservation(
                orderId,
                request.eventId(),
                request.seatIds()
        );
    }
}
