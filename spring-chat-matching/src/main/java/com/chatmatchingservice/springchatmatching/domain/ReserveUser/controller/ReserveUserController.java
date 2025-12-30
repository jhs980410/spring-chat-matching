package com.chatmatchingservice.springchatmatching.domain.ReserveUser.controller;

import com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto.ReserveUserDetailDto;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto.ReserveUserRequest;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto.ReserveUserSummaryDto;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.service.ReserveUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@Tag(
        name = "Reserve User",
        description = """
    예매자 정보 관리 API

    - 로그인 사용자 전용 (/api/me)
    - 사용자당 복수 예매자 관리 가능
    - 주문 시 사용되는 실 예매자 정보 관리
    """
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/reserve-users")
public class ReserveUserController {

    private final ReserveUserService reserveUserService;

    /* =========================
       목록
    ========================= */
    @Operation(summary = "내 예매 목록 조회")
    @GetMapping
    public List<ReserveUserSummaryDto> getMyReserveUsers(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return reserveUserService.getMyReserveUsers(userId);
    }

    /* =========================
       상세
    ========================= */
    @Operation(summary = "예매자 상세 조회")
    @GetMapping("/{id}")
    public ReserveUserDetailDto getReserveUser(
            Authentication auth,
            @PathVariable Long id
    ) {
        Long userId = (Long) auth.getPrincipal();
        return reserveUserService.getReserveUser(userId, id);
    }

    /* =========================
       등록
    ========================= */
    @Operation(summary = "예매자 등록")
    @PostMapping
    public Map<String, Long> createReserveUser(
            Authentication auth,
            @RequestBody @Valid ReserveUserRequest request
    ) {
        Long userId = (Long) auth.getPrincipal();
        Long id = reserveUserService.createReserveUser(userId, request);
        return Map.of("id", id);
    }

    /* =========================
       수정
    ========================= */
    @Operation(summary = "예매자 정보 수정")
    @PutMapping("/{id}")
    public void updateReserveUser(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody @Valid ReserveUserRequest request
    ) {
        Long userId = (Long) auth.getPrincipal();
        reserveUserService.updateReserveUser(userId, id, request);
    }

    /* =========================
       삭제
    ========================= */
    @Operation(summary = "예매자 삭제")
    @DeleteMapping("/{id}")
    public void deleteReserveUser(
            Authentication auth,
            @PathVariable Long id
    ) {
        Long userId = (Long) auth.getPrincipal();
        reserveUserService.deleteReserveUser(userId, id);
    }
}
