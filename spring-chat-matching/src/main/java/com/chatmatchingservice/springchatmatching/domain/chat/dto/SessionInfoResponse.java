package com.chatmatchingservice.springchatmatching.domain.chat.dto;

import java.time.LocalDateTime;

public record SessionInfoResponse(
        Long sessionId,
        String status,
        Long userId,
        Long counselorId,
        Long categoryId,
        LocalDateTime startedAt

) {}

