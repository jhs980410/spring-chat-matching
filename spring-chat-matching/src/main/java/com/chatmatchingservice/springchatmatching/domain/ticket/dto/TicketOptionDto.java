package com.chatmatchingservice.springchatmatching.domain.ticket.dto;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TicketOptionDto {

    private Long ticketId;
    private String name;          // VIP, R석, S석
    private int price;
    private int remainQuantity;

    // 프론트 UX용
    private boolean soldOut;

    public static TicketOptionDto from(Ticket ticket) {
        return TicketOptionDto.builder()
                .ticketId(ticket.getId())
                .name(ticket.getName())
                .price(ticket.getPrice())
                .remainQuantity(ticket.getRemainQuantity())
                .soldOut(ticket.getRemainQuantity() <= 0)
                .build();
    }
}
