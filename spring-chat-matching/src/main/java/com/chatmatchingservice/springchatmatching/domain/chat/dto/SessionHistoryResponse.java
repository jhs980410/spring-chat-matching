package com.chatmatchingservice.springchatmatching.domain.chat.dto;

public record SessionHistoryResponse(
        Long sessionId,
        String status,

        Long userId,
        String userName,

        Long counselorId,
        String counselorName,

        String domainName,
        String categoryName,

        String requestedAt,
        String startedAt,
        String endedAt,
        Long durationSec
) {}