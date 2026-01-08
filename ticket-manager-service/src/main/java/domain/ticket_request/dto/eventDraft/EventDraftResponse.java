package domain.ticket_request.dto.eventDraft;

import domain.ticket_request.entity.DraftStatus;

import java.time.LocalDateTime;

public record EventDraftResponse(
        Long id,
        String title,
        DraftStatus status,
        LocalDateTime createdAt,
        LocalDateTime requestedAt
) {
}
