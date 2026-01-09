package com.chatmatchingservice.hqadmin.domain.approval.dto;

public record ApprovalRequest(
        String reason   // reject 시 필수, approve 시 null 허용
) {}
