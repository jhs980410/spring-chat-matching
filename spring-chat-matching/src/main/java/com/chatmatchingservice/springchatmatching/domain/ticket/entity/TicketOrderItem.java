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
       🔥 연관관계
       ========================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private TicketOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    /* =========================
       🔥 가격 정보
       ========================= */

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private Long unitPrice;

    @Column(nullable = false)
    private Long price;

    /* =========================
       🔥 생성 메서드
       ========================= */
    public static TicketOrderItem create(
            Seat seat,
            Long unitPrice
    ) {
        TicketOrderItem item = new TicketOrderItem();
        item.seat = seat;
        item.quantity = 1;          // 좌석 단위 예매 → 항상 1
        item.unitPrice = unitPrice;
        item.price = unitPrice;
        return item;
    }

    /* =========================
       🔥 연관관계 설정 (Order 전용)
       ========================= */
    void assignOrder(TicketOrder order) {
        this.order = order;
    }
}
