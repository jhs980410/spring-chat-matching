package com.chatmatchingservice.springchatmatching.domain.event.dto;

import java.util.List;

public record SectionResponseDto(
        Long sectionId,
        String code,           // 115, FLOOR_A
        String name,           // 115구역
        String grade,          // VIP / R / S
        int price,             // ticket.price
        int totalSeats,
        int remainSeats,
        List<SeatResponseDto> seats
) {}
