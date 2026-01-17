package com.chatmatchingservice.hqadmin.domain.approval.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ApprovalListResponse(
        Long eventDraftId,
        String title,       // 공연 제목
        String status,      // DraftStatus (String으로 처리하여 Enum 불일치 해결)
        LocalDateTime requestedAt
) {
}