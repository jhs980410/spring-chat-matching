package domain.draft.query.dto;

import domain.draft.entity.DraftStatus;

import java.time.LocalDateTime;

public record EventDraftSummaryResponse(
        Long id,
        String title,
        DraftStatus status,
        LocalDateTime requestedAt,
        LocalDateTime createdAt
) {}