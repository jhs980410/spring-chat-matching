package com.chatmatchingservice.hqadmin.domain.approval.dto;

import com.chatmatchingservice.hqadmin.domain.approval.entity.ApprovalStatus;
import com.chatmatchingservice.hqadmin.domain.approval.entity.EventApprovalEntity;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder // 빌더 패턴 지원
public record ApprovalResponse(
        Long eventDraftId,
        ApprovalStatus status,
        String reason,
        LocalDateTime decidedAt
) {
    /**
     * 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     * [cite: 2026-01-13]
     */

}