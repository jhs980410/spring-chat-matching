package com.chatmatchingservice.hqadmin.domain.approval.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contract_approval", schema = "hq_admin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractApprovalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sales_contract_draft_id", nullable = false)
    private Long salesContractDraftId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private String reason;

    private LocalDateTime decidedAt;

    public static ContractApprovalEntity approve(Long draftId, Long adminId) {
        ContractApprovalEntity entity = new ContractApprovalEntity();
        entity.salesContractDraftId = draftId;
        entity.adminId = adminId;
        entity.status = ApprovalStatus.APPROVED;
        entity.decidedAt = LocalDateTime.now();
        return entity;
    }
}