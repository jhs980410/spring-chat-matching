package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.ticketDraft;

public record TicketDraftResponse(
        Long id,
        String name,
        int price,
        int totalQuantity
) {}