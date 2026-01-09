package domain.draft.query.dto;

public record TicketDraftResponse(
        Long id,
        String name,
        int price,
        int totalQuantity
) {}
