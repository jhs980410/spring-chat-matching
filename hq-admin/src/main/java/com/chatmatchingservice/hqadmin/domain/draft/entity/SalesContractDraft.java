package com.chatmatchingservice.hqadmin.domain.draft.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_contract_draft", catalog = "ticket_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalesContractDraft {

    @Id
    /**
     * Manager 서버(8081)에서 생성된 ID를 그대로 사용하므로
     * @GeneratedValue를 사용하지 않습니다.
     */
    private Long id;

    @Column(name = "partner_draft_id")
    private Long partnerDraftId;

    @Column(name = "domain_id")
    private Long domainId;

    @Column(name = "business_name") // DB의 business_name 컬럼 매핑
    private String businessName;

    @Column(name = "business_number") // DB의 business_number 컬럼 매핑
    private String businessNumber;

    @Column(name = "settlement_email")
    private String settlementEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_method")
    private IssueMethod issueMethod; // ONLINE, ON_SITE, DELIVERY

    @Enumerated(EnumType.STRING)
    private DraftStatus status; // DRAFT, REQUESTED, APPROVED, REJECTED

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * [비즈니스 로직] 최종 승인 처리
     */
    public void approve() {
        this.status = DraftStatus.APPROVED;
    }

    /**
     * [비즈니스 로직] 반려 처리
     */
    public void reject() {
        this.status = DraftStatus.REJECTED;
    }
}