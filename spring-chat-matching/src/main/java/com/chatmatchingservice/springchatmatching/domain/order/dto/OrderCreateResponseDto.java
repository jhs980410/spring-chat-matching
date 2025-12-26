package com.chatmatchingservice.springchatmatching.domain.order.dto;

import java.time.LocalDateTime;

public record OrderCreateResponseDto(
        Long orderId,
        Long totalPrice,
        String status,
        LocalDateTime orderedAt
) {}
