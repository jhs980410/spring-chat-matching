package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.ticketDraft;

public record TicketDraftCreateRequest(
        String name,          // 예: VIP석
        int price,
        int totalQuantity,
        String sectionCode,   // 예: SEC-A (8080의 code 필드)
        String sectionName,   // 예: A구역 (8080의 name 필드)
        String rowLabel       // 예: 1열 (8080의 row_label 필드)
) {}
