package com.chatmatchingservice.springchatmatching.domain.order.dto;

public record OrderSeatItemDto(
        Long ticketId,
        Long seatId,
        int unitPrice
) {}
