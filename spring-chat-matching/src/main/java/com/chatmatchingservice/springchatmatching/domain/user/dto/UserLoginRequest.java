package com.chatmatchingservice.springchatmatching.domain.user.dto;

public record UserLoginRequest(
        String email,
        String password
) {}
