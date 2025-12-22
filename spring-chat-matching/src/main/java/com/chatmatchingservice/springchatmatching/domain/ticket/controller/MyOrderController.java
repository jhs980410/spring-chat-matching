package com.chatmatchingservice.springchatmatching.domain.ticket.controller;

import com.chatmatchingservice.springchatmatching.domain.ticket.dto.MyOrderResponseDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.dto.MyPageHomeResponseDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.service.MyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class MyOrderController {

    private final MyOrderService myOrderService;


    // 1️⃣ 예매 목록
    @GetMapping("/orders")
    public List<MyOrderResponseDto> getMyOrders(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return myOrderService.getMyOrders(userId);
    }

    // 2️⃣ 예매 상세
    @GetMapping("/orders/{orderId}")
    public MyOrderResponseDto getMyOrderDetail(
            @PathVariable Long orderId,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();
        return myOrderService.getMyOrderDetail(userId, orderId);
    }

    // 3️⃣ 마이페이지 홈
    @GetMapping("/home")
    public MyPageHomeResponseDto getMyPageHome(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return myOrderService.getMyPageHome(userId);
    }
}
