package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.eventDraft;

import java.time.LocalDateTime;

public record EventDraftCreateRequest(
        Long domainId,
        String title,
        String description,
        String venue,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String thumbnail
) {
}
