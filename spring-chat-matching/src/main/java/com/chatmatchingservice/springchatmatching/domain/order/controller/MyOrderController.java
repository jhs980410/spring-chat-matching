package com.chatmatchingservice.springchatmatching.domain.order.controller;

import com.chatmatchingservice.springchatmatching.domain.order.dto.MyOrderResponseDto;
import com.chatmatchingservice.springchatmatching.domain.mypage.dto.MyPageHomeResponseDto;
import com.chatmatchingservice.springchatmatching.domain.order.service.MyOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "My Orders",
        description = """
    마이페이지 예매 관리 API

    - 사용자 예매 내역 조회
    - 예매 상세 조회
    - 마이페이지 홈 데이터 조회
    """
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class MyOrderController {

    private final MyOrderService myOrderService;

    // ========================================
    // 예매 목록 조회
    // ========================================
    @Operation(
            summary = "내 예매 목록 조회",
            description = "로그인한 사용자의 전체 예매 내역을 조회하는 API"
    )
    @GetMapping("/orders")
    public List<MyOrderResponseDto> getMyOrders(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return myOrderService.getMyOrders(userId);
    }

    // ========================================
    // 예매 상세 조회
    // ========================================
    @Operation(
            summary = "내 예매 상세 조회",
            description = "특정 예매(orderId)의 상세 정보를 조회하는 API"
    )
    @GetMapping("/orders/{orderId}")
    public MyOrderResponseDto getMyOrderDetail(
            @PathVariable Long orderId,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();
        return myOrderService.getMyOrderDetail(userId, orderId);
    }

    // ========================================
    // 마이페이지 홈
    // ========================================
    @Operation(
            summary = "마이페이지 홈 조회",
            description = "마이페이지 메인 화면에 사용되는 요약 데이터 조회"
    )
    @GetMapping("/home")
    public MyPageHomeResponseDto getMyPageHome(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return myOrderService.getMyPageHome(userId);
    }
}
