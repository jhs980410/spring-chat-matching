package com.chatmatchingservice.springchatmatching.global.error;

public record ErrorResponse(
        int status,
        String code,
        String message
) {}
