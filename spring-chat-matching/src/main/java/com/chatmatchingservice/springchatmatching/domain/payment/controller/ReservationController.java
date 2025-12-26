package com.chatmatchingservice.springchatmatching.domain.payment.controller;

import com.chatmatchingservice.springchatmatching.domain.event.dto.SectionResponseDto;
import com.chatmatchingservice.springchatmatching.domain.payment.service.ReservationService;
import com.chatmatchingservice.springchatmatching.domain.ticket.service.SeatQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class ReservationController {

    private final ReservationService reservationService;
    private final SeatQueryService seatQueryService;

    @GetMapping("/{eventId}/seats")
    public List<SectionResponseDto> seats(@PathVariable Long eventId) {
        return seatQueryService.getSectionsWithSeats(eventId);
    }

    @PostMapping("/{eventId}/reserve")
    public void reserve(
            @PathVariable Long eventId,
            @RequestBody List<Long> seatIds,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();
        reservationService.prepareReservation(userId, eventId, seatIds);
    }
}
