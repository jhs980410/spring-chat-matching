package com.chatmatchingservice.hqadmin.domain.publish.dto;

// 승인/반려 요청 DTO
public record ContractApprovalRequest(
        String reason // 반려 시 사유
) {}