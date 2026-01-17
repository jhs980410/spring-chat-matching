package com.chatmatchingservice.hqadmin.domain.draft.dto;

import com.chatmatchingservice.hqadmin.domain.draft.entity.DraftStatus;
import com.chatmatchingservice.hqadmin.domain.draft.entity.IssueMethod;

import java.time.LocalDateTime;

public record SalesContractDraftRecord(
        Long id,
        String businessName,
        String businessNumber,
        String settlementEmail,
        IssueMethod issueMethod,
        DraftStatus status,
        LocalDateTime requestedAt
) {}