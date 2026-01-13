package com.chatmatchingservice.hqadmin.domain.draft.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_draft",schema = "ticket_manager")
@Getter
public class EventDraftEntity {

    @Id
    private Long id;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "domain_id")
    private Long domainId;

    private String title;
    private String description;
    private String venue;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    private String thumbnail;

    @Enumerated(EnumType.STRING)
    private DraftStatus status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    public void approve() { this.status = DraftStatus.APPROVED; }
    public void reject() { this.status = DraftStatus.REJECTED; }
}
