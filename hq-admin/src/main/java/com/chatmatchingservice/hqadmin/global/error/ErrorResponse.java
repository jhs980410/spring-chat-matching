package com.chatmatchingservice.hqadmin.global.error;

public record ErrorResponse(
        int status,
        String code,
        String message
) {}
