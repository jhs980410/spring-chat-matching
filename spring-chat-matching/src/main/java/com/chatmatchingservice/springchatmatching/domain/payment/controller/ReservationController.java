package com.chatmatchingservice.springchatmatching.domain.payment.controller;

import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateRequestDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.OrderCreateResponseDto;
import com.chatmatchingservice.springchatmatching.domain.payment.service.ReservationService;
import com.chatmatchingservice.springchatmatching.domain.ticket.dto.SeatReserveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class ReservationController {

    private final ReservationService reservationService;


    /**
     * 주문 생성 (PENDING)
     */
    @PostMapping
    public OrderCreateResponseDto createOrder(
            @RequestBody OrderCreateRequestDto request,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();
        return reservationService.createOrder(userId, request);
    }

    /**
     * 좌석 락 (orderId 기준)
     */
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
