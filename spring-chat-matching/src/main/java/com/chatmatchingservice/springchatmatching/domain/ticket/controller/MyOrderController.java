package com.chatmatchingservice.springchatmatching.domain.ticket.controller;

import com.chatmatchingservice.springchatmatching.domain.ticket.dto.MyOrderResponseDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.service.MyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class MyOrderController {

    private final MyOrderService myOrderService;

    @GetMapping("/orders")
    public List<MyOrderResponseDto> getMyOrders(
            Authentication auth) {

        Long userId = (Long) auth.getPrincipal();
        return myOrderService.getMyOrders(userId);
    }
}
