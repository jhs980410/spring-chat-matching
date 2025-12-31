package com.chatmatchingservice.springchatmatching.auth.controller;

import com.chatmatchingservice.springchatmatching.auth.dto.AuthResponse;
import com.chatmatchingservice.springchatmatching.auth.service.AuthService;
import com.chatmatchingservice.springchatmatching.domain.user.dto.UserSignupRequest;
import com.chatmatchingservice.springchatmatching.domain.user.dto.UserLoginRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorSignupRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorLoginRequest;
import com.chatmatchingservice.springchatmatching.global.auth.jwt.CookieUtil; // 추가
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Tag(
        name = "Auth",
        description = """
    인증 및 토큰 관리 API

    - 사용자 / 상담사 회원가입 및 로그인
    - JWT 기반 인증 (HttpOnly Cookie)
    - AccessToken 재발급 (Refresh Token)
    - 로그아웃 처리
    """
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;


    // ==============================
    // USER SIGNUP
    // ==============================
    @Operation(summary = "사용자 회원가입")
    @PostMapping("/user/signup")
    public ResponseEntity<Void> userSignup(@RequestBody UserSignupRequest req) {
        log.info("[API] User Signup: {}", req.email());
        authService.userSignup(req);
        return ResponseEntity.ok().build();
    }


    // ==============================
    // USER LOGIN
    // ==============================
    @Operation(summary = "사용자 로그인")
    @PostMapping("/user/login")
    public ResponseEntity<AuthResponse> userLogin(
            @RequestBody UserLoginRequest req,
            HttpServletResponse response
    ) {
        log.info("[API] User Login attempt: {}", req.email());
        AuthResponse res = authService.userLogin(req);
        cookieUtil.addTokenCookiesToResponse(response, res);
        return ResponseEntity.ok(res);
    }
    @Operation(summary = "토큰 유효확인")
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(HttpServletRequest request) {
        // 1. 요청의 쿠키나 헤더에 있는 토큰을 파싱해서 유저 정보를 가져옴
        // 2. AuthService에서 해당 토큰이 유효한지 확인하고 유저 정보를 담은 AuthResponse 반환
        AuthResponse res = authService.getCurrentUserInfo(request);
        return ResponseEntity.ok(res);
    }

    // ==============================
    // COUNSELOR SIGNUP
    // ==============================
    @Operation(summary = "상담사 회원가입")
    @PostMapping("/counselor/signup")
    public ResponseEntity<Void> counselorSignup(@RequestBody CounselorSignupRequest req) {
        log.info("[API] Counselor Signup: {}", req.email());
        authService.counselorSignup(req);
        return ResponseEntity.ok().build();
    }


    // ==============================
    // COUNSELOR LOGIN
    // ==============================
    @Operation(summary = "상담사 로그인")
    @PostMapping("/counselor/login")
    public ResponseEntity<AuthResponse> counselorLogin(
            @RequestBody CounselorLoginRequest req,
            HttpServletResponse response
    ) {
        log.info("[API] Counselor Login attempt: {}", req.email());
        AuthResponse res = authService.counselorLogin(req);
        cookieUtil.addTokenCookiesToResponse(response, res);
        return ResponseEntity.ok(res);
    }


    // =======================================================
    // 🔥 REFRESH TOKEN (AccessToken 재발급)
    // =======================================================
    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        AuthResponse res = authService.refresh(request);

        // 새 Access 쿠키 갱신
        cookieUtil.updateAccessToken(response, res.accessToken());

        return ResponseEntity.ok(res);
    }


    // =======================================================
    // 🔥 LOGOUT (쿠키 삭제)
    // =======================================================
    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        log.info("[API] Logout");

        cookieUtil.clearAuthCookies(response);

        return ResponseEntity.ok().build();
    }
}
