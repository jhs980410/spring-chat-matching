package domain.ticket_request.entity;

import domain.manager.entity.TicketManager;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_draft")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private TicketManager manager;

    @Column(name = "domain_id", nullable = false)
    private Long domainId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String venue;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DraftStatus status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /* =========================
       생성 로직 (정적 팩토리)
       ========================= */
    public static EventDraft create(
            TicketManager manager,
            Long domainId,
            String title,
            String description,
            String venue,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String thumbnail
    ) {
        EventDraft draft = new EventDraft();
        draft.manager = manager;
        draft.domainId = domainId;
        draft.title = title;
        draft.description = description;
        draft.venue = venue;
        draft.startAt = startAt;
        draft.endAt = endAt;
        draft.thumbnail = thumbnail;

        draft.status = DraftStatus.DRAFT;
        draft.createdAt = LocalDateTime.now();

        return draft;
    }

    /* =========================
       상태 전이: 요청
       ========================= */
    public void request() {
        if (this.status != DraftStatus.DRAFT) {
            throw new IllegalStateException(
                    "DRAFT 상태에서만 요청할 수 있습니다."
            );
        }

        this.status = DraftStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
    }
}
