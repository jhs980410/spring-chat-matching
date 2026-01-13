package com.chatmatchingservice.hqadmin.domain.draft.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
/**
 * [수정 포인트]
 * MySQL에서는 schema 대신 catalog를 사용해야 '스키마.테이블' 형태로 쿼리가 정확히 생성됩니다.
 * 또한 하이버네이트가 점(.)을 백틱으로 감싸지 않도록 주의해야 합니다.
 */
@Table(name = "event_draft", catalog = "ticket_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티 기본 생성자 추가
public class EventDraftEntity {

    @Id
    /**
     * 8081(매니저)에서 생성한 ID를 그대로 조회하므로
     * 자동 생성 전략(@GeneratedValue)은 사용하지 않습니다.
     */
    private Long id;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "sales_contract_draft_id")
    private Long salesContractDraftId;
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



    public void approve() {
        this.status = DraftStatus.APPROVED;
    }

    public void reject() {
        this.status = DraftStatus.REJECTED;
    }
}