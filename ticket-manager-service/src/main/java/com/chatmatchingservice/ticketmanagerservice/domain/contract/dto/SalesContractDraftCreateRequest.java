package com.chatmatchingservice.ticketmanagerservice.domain.contract.dto;

public record SalesContractDraftCreateRequest(
        String businessName,
        String businessNumber,
        String ceoName,
        String contactEmail,
        String contactPhone
) {}
