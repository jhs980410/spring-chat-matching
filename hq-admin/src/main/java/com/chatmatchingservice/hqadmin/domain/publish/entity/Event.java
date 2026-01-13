package com.chatmatchingservice.hqadmin.domain.publish.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event", schema = "chatmatching")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "domain_id", nullable = false)
    private Long domainId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 200)
    private String venue;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false, length = 500)
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /* =========================
       생성 로직 (Publish 전용)
       ========================= */
    public static Event create(
            Long domainId,
            String title,
            String description,
            String venue,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String thumbnail
    ) {
        Event event = new Event();
        event.domainId = domainId;
        event.title = title;
        event.description = description;
        event.venue = venue;
        event.startAt = startAt;
        event.endAt = endAt;
        event.thumbnail = thumbnail;

        event.status = EventStatus.OPEN;
        event.createdAt = LocalDateTime.now();

        return event;
    }
}
