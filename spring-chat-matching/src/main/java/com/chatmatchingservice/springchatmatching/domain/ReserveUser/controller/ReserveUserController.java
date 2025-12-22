package com.chatmatchingservice.springchatmatching.domain.ReserveUser.controller;

import com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto.ReserveUserDetailDto;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto.ReserveUserRequest;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto.ReserveUserSummaryDto;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.service.ReserveUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/reserve-users")
public class ReserveUserController {

    private final ReserveUserService reserveUserService;

    /* =========================
       목록
    ========================= */
    @GetMapping
    public List<ReserveUserSummaryDto> getMyReserveUsers(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return reserveUserService.getMyReserveUsers(userId);
    }

    /* =========================
       상세
    ========================= */
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
    @DeleteMapping("/{id}")
    public void deleteReserveUser(
            Authentication auth,
            @PathVariable Long id
    ) {
        Long userId = (Long) auth.getPrincipal();
        reserveUserService.deleteReserveUser(userId, id);
    }
}
