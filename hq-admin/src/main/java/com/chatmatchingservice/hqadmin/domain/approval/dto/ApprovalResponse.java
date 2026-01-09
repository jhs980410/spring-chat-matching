package com.chatmatchingservice.hqadmin.domain.approval.dto;



import com.chatmatchingservice.hqadmin.domain.approval.entity.ApprovalStatus;

import java.time.LocalDateTime;

public record ApprovalResponse(
        Long eventDraftId,
        ApprovalStatus status,
        String reason,
        LocalDateTime decidedAt
) {}
