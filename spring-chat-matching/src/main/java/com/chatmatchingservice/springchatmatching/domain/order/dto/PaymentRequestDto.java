package com.chatmatchingservice.springchatmatching.domain.order.dto;

import com.chatmatchingservice.springchatmatching.domain.order.entity.PaymentMethod;

public record PaymentRequestDto(
        Long orderId,
        PaymentMethod method   // CARD / KAKAO / NAVER / BANK
) {}
