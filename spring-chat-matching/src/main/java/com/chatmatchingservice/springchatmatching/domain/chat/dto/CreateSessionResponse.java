package com.chatmatchingservice.springchatmatching.domain.chat.dto;

public record CreateSessionResponse(
        Long sessionId,
        String status
) {}
