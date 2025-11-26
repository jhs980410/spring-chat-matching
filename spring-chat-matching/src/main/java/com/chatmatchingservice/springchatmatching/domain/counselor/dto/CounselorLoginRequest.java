package com.chatmatchingservice.springchatmatching.domain.counselor.dto;

public record CounselorLoginRequest(
        String email,
        String password
) {}
