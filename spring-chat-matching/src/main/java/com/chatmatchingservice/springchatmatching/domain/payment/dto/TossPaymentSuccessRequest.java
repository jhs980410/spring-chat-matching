package com.chatmatchingservice.springchatmatching.domain.payment.dto;

public record TossPaymentSuccessRequest(
        String paymentKey,
        String orderId,
        Long amount
) {}
