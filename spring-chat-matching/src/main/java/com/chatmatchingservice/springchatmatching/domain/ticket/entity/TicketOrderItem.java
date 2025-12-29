package com.chatmatchingservice.springchatmatching.domain.ticket.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "ticket_order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       연관관계
       ========================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private TicketOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;   // ✅ 반드시 Ticket

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    /* =========================
       가격 정보
       ========================= */

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private Long unitPrice;

    @Column(nullable = false)
    private Long price;

    /* =========================
       생성 메서드
       ========================= */
    public static TicketOrderItem create(Seat seat) {
        Ticket ticket = seat.getSection().getTicket();

        TicketOrderItem item = new TicketOrderItem();
        item.seat = seat;
        item.ticket = ticket;
        item.quantity = 1;
        item.unitPrice = ticket.getPrice();
        item.price = item.unitPrice;
        return item;
    }

    void assignOrder(TicketOrder order) {
        this.order = order;
    }
}
