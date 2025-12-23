package com.chatmatchingservice.springchatmatching.domain.order.dto;

import java.util.List;

public record SeatLockResultDto(
        boolean success,
        List<Long> lockedSeatIds,
        List<Long> failedSeatIds,
        String message
) {}