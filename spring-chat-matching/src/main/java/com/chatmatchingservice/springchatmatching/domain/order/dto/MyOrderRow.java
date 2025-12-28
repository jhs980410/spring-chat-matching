package com.chatmatchingservice.springchatmatching.domain.order.dto;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyOrderRow {

    private Long orderId;
    private TicketOrderStatus orderStatus;
    private LocalDateTime orderedAt;
    private Long totalPrice;

    private Long eventId;
    private String eventTitle;
    private String thumbnail;
    private String venue;
    private LocalDateTime startAt;

    private String ticketName;
    private Integer quantity;
    private Long unitPrice;
}

