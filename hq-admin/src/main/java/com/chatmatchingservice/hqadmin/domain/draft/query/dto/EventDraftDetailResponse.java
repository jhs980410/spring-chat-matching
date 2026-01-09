package com.chatmatchingservice.hqadmin.domain.draft.query.dto;


import com.chatmatchingservice.hqadmin.domain.draft.entity.DraftStatus;

import java.time.LocalDateTime;
import java.util.List;

public record EventDraftDetailResponse(
        Long id,
        Long managerId,
        Long domainId,
        String title,
        String description,
        String venue,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String thumbnail,
        DraftStatus status,
        LocalDateTime requestedAt,
        LocalDateTime createdAt,
        List<TicketDraftResponse> tickets
) {}