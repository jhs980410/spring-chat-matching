package com.chatmatchingservice.springchatmatching.domain.order.dto;

import com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto.ReserveUserSummaryDto;

import java.util.List;

public record OrderDetailResponseDto(
        Long orderId,
        String status,
        Long totalPrice,
        ReserveUserSummaryDto reserveUser,
        List<OrderSeatDetailDto> seats,
        PaymentResponseDto payment
) {}
