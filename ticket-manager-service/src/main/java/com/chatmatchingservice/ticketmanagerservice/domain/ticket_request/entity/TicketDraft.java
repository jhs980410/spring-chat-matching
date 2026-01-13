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
    private String name; // 예: VIP석, S석

    @Column(nullable = false)
    private int price;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    // --- 8080 운영 DB(venue_section, seat) 연동을 위한 추가 필드 ---

    @Column(name = "section_code", nullable = false)
    private String sectionCode; // 예: SEC-A, FLOOR-1

    @Column(name = "section_name", nullable = false)
    private String sectionName; // 예: A구역, 1층 플로어

    @Column(name = "row_label")
    private String rowLabel;    // 예: A열, 1열 (null 허용 가능)

    // -------------------------------------------------------

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /* =========================
      정적 팩토리 (수정됨)
      ========================= */
    public static TicketDraft create(
            EventDraft eventDraft,
            String name,
            int price,
            int totalQuantity,
            String sectionCode,
            String sectionName,
            String rowLabel
    ) {
        TicketDraft draft = new TicketDraft();
        draft.eventDraft = eventDraft;
        draft.name = name;
        draft.price = price;
        draft.totalQuantity = totalQuantity;

        // 추가된 필드 할당
        draft.sectionCode = sectionCode;
        draft.sectionName = sectionName;
        draft.rowLabel = rowLabel;

        draft.createdAt = LocalDateTime.now();
        return draft;
    }
}