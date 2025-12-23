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

    /* =========================
       🔥 도메인 생성 메서드
       ========================= */
    public static TicketOrderItem create(
            TicketOrder order,
            Ticket ticket,
            int unitPrice
    ) {
        TicketOrderItem item = TicketOrderItem.builder()
                .order(order)
                .ticket(ticket)
                .quantity(1)          // 좌석 단위 예매 → 기본 1
                .unitPrice(unitPrice)
                .price(unitPrice)     // quantity * unitPrice
                .build();

        order.addItem(item); // 양방향 연관관계 보장
        return item;
    }

    void setOrder(TicketOrder order) {
        this.order = order;
    }
}
