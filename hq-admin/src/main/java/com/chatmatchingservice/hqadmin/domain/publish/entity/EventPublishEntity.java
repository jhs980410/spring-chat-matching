package com.chatmatchingservice.hqadmin.domain.publish.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "event_publish",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_publish_draft",
                        columnNames = "event_draft_id"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventPublishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_draft_id", nullable = false)
    private Long eventDraftId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    public static EventPublishEntity of(Long draftId, Long eventId) {
        EventPublishEntity e = new EventPublishEntity();
        e.eventDraftId = draftId;
        e.eventId = eventId;
        e.publishedAt = LocalDateTime.now();
        return e;
    }
}
