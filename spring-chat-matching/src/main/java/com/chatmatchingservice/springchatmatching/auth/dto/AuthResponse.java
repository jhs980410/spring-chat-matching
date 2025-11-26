package com.chatmatchingservice.springchatmatching.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long id,
        String role
) {}
