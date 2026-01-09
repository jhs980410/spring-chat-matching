package com.chatmatchingservice.hqadmin.domain.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long id,
        String role
) {}
