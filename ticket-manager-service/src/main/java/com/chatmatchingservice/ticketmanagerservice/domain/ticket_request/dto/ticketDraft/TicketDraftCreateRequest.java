package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.ticketDraft;

public record TicketDraftCreateRequest(
        String name,
        int price,
        int totalQuantity
) {}

