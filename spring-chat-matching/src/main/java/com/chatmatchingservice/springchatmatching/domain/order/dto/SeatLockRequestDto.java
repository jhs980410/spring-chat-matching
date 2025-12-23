package com.chatmatchingservice.springchatmatching.domain.order.dto;

import java.util.List;

public record SeatLockRequestDto(
        Long eventId,
        List<Long> seatIds
) {}
