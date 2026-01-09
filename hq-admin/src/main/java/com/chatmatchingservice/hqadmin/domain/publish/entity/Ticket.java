package com.chatmatchingservice.hqadmin.domain.publish.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "remain_quantity", nullable = false)
    private int remainQuantity;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /* =========================
       생성 로직 (Publish 전용)
       ========================= */
    public static Ticket create(
            Event event,
            String name,
            int price,
            int totalQuantity
    ) {
        Ticket ticket = new Ticket();
        ticket.event = event;
        ticket.name = name;
        ticket.price = price;
        ticket.totalQuantity = totalQuantity;
        ticket.remainQuantity = totalQuantity; // 최초는 전체 수량
        ticket.createdAt = LocalDateTime.now();

        return ticket;
    }
}
