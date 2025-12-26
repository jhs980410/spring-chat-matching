package com.chatmatchingservice.springchatmatching.domain.payment.controller;

import com.chatmatchingservice.springchatmatching.domain.order.dto.PaymentResponseDto;
import com.chatmatchingservice.springchatmatching.domain.payment.dto.TossPaymentFailRequest;
import com.chatmatchingservice.springchatmatching.domain.payment.dto.TossPaymentSuccessRequest;
import com.chatmatchingservice.springchatmatching.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class TossPaymentController {

    private final PaymentService paymentService;

    @PostMapping("/success")
    public PaymentResponseDto success(
            @RequestBody TossPaymentSuccessRequest request,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();
        return paymentService.confirmPayment(userId, request);
    }

    @PostMapping("/fail")
    public void fail(
            @RequestBody TossPaymentFailRequest request,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();
        paymentService.failPayment(userId, request);
    }
}
