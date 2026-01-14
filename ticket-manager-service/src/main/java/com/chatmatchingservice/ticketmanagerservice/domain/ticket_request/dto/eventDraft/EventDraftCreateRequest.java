package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.eventDraft;

import java.time.LocalDateTime;

public record EventDraftCreateRequest(
        Long domainId,
        String title,
        Long categoryId,
        String description,
        String venue,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String thumbnail
) {
}
