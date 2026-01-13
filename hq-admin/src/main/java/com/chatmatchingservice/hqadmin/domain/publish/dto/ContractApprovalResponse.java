package com.chatmatchingservice.hqadmin.domain.publish.dto;

import java.time.LocalDateTime;

// 응답 DTO
public record ContractApprovalResponse(
        Long contractDraftId,
        String status,
        LocalDateTime decidedAt
) {}
