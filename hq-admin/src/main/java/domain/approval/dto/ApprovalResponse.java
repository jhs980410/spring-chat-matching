package domain.approval.dto;

import domain.approval.entity.ApprovalStatus;

import java.time.LocalDateTime;

public record ApprovalResponse(
        Long eventDraftId,
        ApprovalStatus status,
        String reason,
        LocalDateTime decidedAt
) {}
