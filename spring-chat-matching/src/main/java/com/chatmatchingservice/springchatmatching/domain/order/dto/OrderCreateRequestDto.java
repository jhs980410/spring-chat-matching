package com.chatmatchingservice.springchatmatching.domain.order.dto;

import java.util.List;

public record OrderCreateRequestDto(
        Long eventId,
        Long reserveUserId,
        List<OrderSeatItemDto> items
) {}