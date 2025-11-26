package com.chatmatchingservice.springchatmatching.domain.user.dto;

public record UserSignupRequest(
        String email,
        String password,
        String nickname
) {}
