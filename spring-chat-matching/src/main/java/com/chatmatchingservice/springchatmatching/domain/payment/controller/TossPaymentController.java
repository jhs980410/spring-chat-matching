package com.chatmatchingservice.springchatmatching.domain.payment.controller;

import com.chatmatchingservice.springchatmatching.domain.order.dto.PaymentResponseDto;
import com.chatmatchingservice.springchatmatching.domain.payment.dto.TossPaymentFailRequest;
import com.chatmatchingservice.springchatmatching.domain.payment.dto.TossPaymentSuccessRequest;
import com.chatmatchingservice.springchatmatching.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Payment (Toss)",
        description = """
    Toss 결제 처리 API

    - Toss Payments 결제 승인(confirm)
    - 결제 실패 처리
    - 예매 주문 확정/실패 최종 단계
    """
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class TossPaymentController {

    private final PaymentService paymentService;

    // ========================================
    // Toss 결제 승인 (CONFIRM)
    // ========================================
    @Operation(
            summary = "Toss 결제 승인",
            description = """
        Toss Payments 결제 성공 후 서버에 결제 승인(confirm)을 요청하는 API

        - 결제 승인 검증
        - 주문 상태 확정 (PAID)
        - 좌석 예매 최종 확정
        """
    )
    @PostMapping("/confirm")
    public PaymentResponseDto confirm(
            @RequestBody TossPaymentSuccessRequest request,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();
        return paymentService.confirmPayment(userId, request);
    }

    // ========================================
    // Toss 결제 실패
    // ========================================
    @Operation(
            summary = "Toss 결제 실패 처리",
            description = """
        Toss Payments 결제 실패 시 호출되는 API

        - 결제 실패 사유 기록
        - 주문 상태 실패 처리
        - 좌석 Lock 해제
        """
    )
    @PostMapping("/fail")
    public void fail(
            @RequestBody TossPaymentFailRequest request,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();
        paymentService.failPayment(userId, request);
    }
}
