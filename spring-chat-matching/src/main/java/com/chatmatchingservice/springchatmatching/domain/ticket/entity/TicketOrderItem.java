package com.chatmatchingservice.springchatmatching.domain.ticket.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "ticket_order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TicketOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private TicketOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int price;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;
    void setOrder(TicketOrder order) {
        this.order = order;
    }
}
