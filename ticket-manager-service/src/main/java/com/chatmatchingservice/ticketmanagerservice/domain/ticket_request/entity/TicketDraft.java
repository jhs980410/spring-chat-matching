package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_draft")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_draft_id", nullable = false)
    private EventDraft eventDraft;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /* =========================
      정적 팩토리
      ========================= */
    public static TicketDraft create(
            EventDraft eventDraft,
            String name,
            int price,
            int totalQuantity
    ) {
        TicketDraft draft = new TicketDraft();
        draft.eventDraft = eventDraft;
        draft.name = name;
        draft.price = price;
        draft.totalQuantity = totalQuantity;
        draft.createdAt = LocalDateTime.now();
        return draft;
    }

}
