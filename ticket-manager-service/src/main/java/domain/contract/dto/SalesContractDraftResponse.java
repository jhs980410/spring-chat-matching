package domain.contract.dto;

import domain.contract.entity.ContractDraftStatus;

import java.time.LocalDateTime;

public record SalesContractDraftResponse(
        Long id,
        String businessName,
        ContractDraftStatus status,
        LocalDateTime createdAt,
        LocalDateTime requestedAt
) {}
