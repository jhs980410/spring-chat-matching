package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.eventDraft;

import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.ticketDraft.TicketDraftResponse;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.entity.DraftStatus;

import java.time.LocalDateTime;
import java.util.List;

public record EventDraftDetailResponse(
        Long id,
        String title,
        String description,
        String venue,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String thumbnail,
        DraftStatus status,
        LocalDateTime createdAt,
        LocalDateTime requestedAt,
        List<TicketDraftResponse> tickets
) {}
