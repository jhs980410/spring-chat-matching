package domain.approval.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "event_approval",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_approval_draft",
                        columnNames = "event_draft_id"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventApprovalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       승인 대상 Draft ID (FK )
       ========================= */
    @Column(name = "event_draft_id", nullable = false)
    private Long eventDraftId;

    /* =========================
       승인자 (HQ Admin)
       ========================= */
    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status;

    @Column(length = 500)
    private String reason;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt;

    /* =========================
       팩토리
       ========================= */
    public static EventApprovalEntity approve(Long draftId, Long adminId) {
        EventApprovalEntity e = new EventApprovalEntity();
        e.eventDraftId = draftId;
        e.adminId = adminId;
        e.status = ApprovalStatus.APPROVED;
        e.decidedAt = LocalDateTime.now();
        return e;
    }

    public static EventApprovalEntity reject(
            Long draftId,
            Long adminId,
            String reason
    ) {
        EventApprovalEntity e = new EventApprovalEntity();
        e.eventDraftId = draftId;
        e.adminId = adminId;
        e.status = ApprovalStatus.REJECTED;
        e.reason = reason;
        e.decidedAt = LocalDateTime.now();
        return e;
    }
}
