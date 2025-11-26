package com.chatmatchingservice.springchatmatching.auth.controller;

import com.chatmatchingservice.springchatmatching.auth.dto.AuthResponse;
import com.chatmatchingservice.springchatmatching.auth.service.AuthService;
import com.chatmatchingservice.springchatmatching.domain.user.dto.UserSignupRequest;
import com.chatmatchingservice.springchatmatching.domain.user.dto.UserLoginRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorSignupRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // ================================
    // USER SIGNUP
    // ================================
    @PostMapping("/user/signup")
    public ResponseEntity<Void> userSignup(@RequestBody UserSignupRequest req) {
        log.info("[API] User Signup: {}", req.email());
        authService.userSignup(req);
        return ResponseEntity.ok().build();
    }

    // ================================
    // USER LOGIN
    // ================================
    @PostMapping("/user/login")
    public ResponseEntity<AuthResponse> userLogin(@RequestBody UserLoginRequest req) {
        log.info("[API] User Login attempt: {}", req.email());
        AuthResponse res = authService.userLogin(req);
        return ResponseEntity.ok(res);
    }

    // ================================
    // COUNSELOR SIGNUP
    // ================================
    @PostMapping("/counselor/signup")
    public ResponseEntity<Void> counselorSignup(@RequestBody CounselorSignupRequest req) {
        log.info("[API] Counselor Signup: {}", req.email());
        authService.counselorSignup(req);
        return ResponseEntity.ok().build();
    }

    // ================================
    // COUNSELOR LOGIN
    // ================================
    @PostMapping("/counselor/login")
    public ResponseEntity<AuthResponse> counselorLogin(@RequestBody CounselorLoginRequest req) {
        log.info("[API] Counselor Login attempt: {}", req.email());
        AuthResponse res = authService.counselorLogin(req);
        return ResponseEntity.ok(res);
    }
}
