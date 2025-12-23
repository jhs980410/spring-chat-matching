package com.chatmatchingservice.springchatmatching.domain.event.dto;

public record SeatResponseDto(
        Long seatId,
        String rowLabel,   // A열
        int seatNumber,    // 12번
        SeatStatus status
) {}
