package com.chatmatchingservice.springchatmatching.domain.payment.dto;

public record TossPaymentFailRequest(
        String orderId,
        String code,
        String message
) {}
