package com.chatmatchingservice.springchatmatching.domain.order.dto;

import java.time.LocalDateTime;

public record PaymentResponseDto(
        Long paymentId,
        Long orderId,
        Long amount,
        String status,        // PAID / FAILED / REFUNDED
        LocalDateTime paidAt
) {}
