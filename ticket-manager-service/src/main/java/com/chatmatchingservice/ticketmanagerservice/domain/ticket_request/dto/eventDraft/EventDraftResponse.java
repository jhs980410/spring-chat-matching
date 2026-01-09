package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.eventDraft;

import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.entity.DraftStatus;

import java.time.LocalDateTime;

public record EventDraftResponse(
        Long id,
        String title,
        DraftStatus status,
        LocalDateTime createdAt,
        LocalDateTime requestedAt
) {
}
