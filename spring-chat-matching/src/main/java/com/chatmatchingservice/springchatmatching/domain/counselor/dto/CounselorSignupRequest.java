package com.chatmatchingservice.springchatmatching.domain.counselor.dto;

public record CounselorSignupRequest(
        String email,
        String password,
        String name
) {}
