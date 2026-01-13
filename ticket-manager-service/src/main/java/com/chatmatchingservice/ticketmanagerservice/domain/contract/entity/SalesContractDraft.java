package com.chatmatchingservice.ticketmanagerservice.domain.contract.entity;

import com.chatmatchingservice.ticketmanagerservice.domain.manager.entity.TicketManager;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales_contract_draft")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalesContractDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       계약 요청 주체 (티켓매니저)
       ========================= */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private TicketManager manager;

    /* =========================
       계약 기본 정보
       ========================= */
    @Column(nullable = false)
    private String businessName;        // 사업자명 / 기획사명

    @Column(nullable = false)
    private String businessNumber;      // 사업자번호

    @Column(nullable = false)
    private String ceoName;

    @Column(nullable = false)
    private String contactEmail;

    @Column(nullable = false)
    private String contactPhone;

    @Column(name = "partner_draft_id", nullable = false)
    private Long partnerDraftId;

    @Column(name = "domain_id", nullable = false)
    private Long domainId;

    @Column(nullable = false)
    private String settlementEmail;

    @Column(nullable = false)
    private String salesReportEmail;

    @Column(nullable = false)
    private String taxEmail;

    @Column(name = "issue_method", nullable = false)
    private String issueMethod; // DB상 ENUM이지만 String으로 우선 매핑
    /* =========================
       상태
       ========================= */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractDraftStatus status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /* =========================
       생성
       ========================= */
    public static SalesContractDraft create(
            TicketManager manager,
            Long partnerDraftId,
            Long domainId,
            String businessName,
            String businessNumber,
            String ceoName,
            String contactEmail,
            String contactPhone,
            String settlementEmail,
            String salesReportEmail,
            String taxEmail,
            String issueMethod
    ) {
        SalesContractDraft draft = new SalesContractDraft();
        draft.manager = manager;
        draft.partnerDraftId = partnerDraftId;
        draft.domainId = domainId;
        draft.businessName = businessName;
        draft.businessNumber = businessNumber;
        draft.ceoName = ceoName;
        draft.contactEmail = contactEmail;
        draft.contactPhone = contactPhone;
        draft.settlementEmail = settlementEmail;
        draft.salesReportEmail = salesReportEmail;
        draft.taxEmail = taxEmail;
        draft.issueMethod = issueMethod; // 'ONLINE', 'ON_SITE', 'DELIVERY' 중 하나

        draft.status = ContractDraftStatus.DRAFT;
        draft.createdAt = LocalDateTime.now();
        return draft;
    }

    /* =========================
       승인 요청
       ========================= */
    public void request() {
        if (this.status != ContractDraftStatus.DRAFT) {
            throw new IllegalStateException("DRAFT 상태에서만 요청 가능합니다.");
        }
        this.status = ContractDraftStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
    }
}
