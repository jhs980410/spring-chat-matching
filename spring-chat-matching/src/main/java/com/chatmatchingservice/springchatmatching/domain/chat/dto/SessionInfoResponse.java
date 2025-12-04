package com.chatmatchingservice.springchatmatching.domain.chat.dto;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionEndReason;

import java.time.LocalDateTime;
public record SessionInfoResponse(
        Long sessionId,
        String status,

        Long userId,
        String userName,
        String userEmail,

        Long counselorId,

        Long domainId,
        String domainName,

        Long categoryId,
        String categoryName,

        LocalDateTime requestedAt,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Long durationSec,
        SessionEndReason endReason,

        Integer satisfactionScore,
        Integer afterCallSec,
        String feedback
) {

    public static SessionInfoResponse empty() {
        return new SessionInfoResponse(
                null,
                "NONE",
                null, null, null,
                null,
                null, null,
                null, null,
                null, null, null, null, null,
                null, null, null
        );
    }
}
