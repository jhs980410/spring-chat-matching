package com.chatmatchingservice.springchatmatching.domain.order.dto;

public record OrderSeatDetailDto(
        String sectionName,
        String rowLabel,
        int seatNumber,
        Long price
) {}