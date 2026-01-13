package com.chatmatchingservice.hqadmin.domain.approval.service;

import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalRequest;
import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalResponse;
import com.chatmatchingservice.hqadmin.domain.approval.entity.ApprovalStatus;
import com.chatmatchingservice.hqadmin.domain.approval.entity.EventApprovalEntity;
import com.chatmatchingservice.hqadmin.domain.approval.repository.EventApprovalRepository;
import com.chatmatchingservice.hqadmin.domain.draft.entity.DraftStatus;
import com.chatmatchingservice.hqadmin.domain.draft.entity.EventDraftEntity;
import com.chatmatchingservice.hqadmin.domain.draft.entity.SalesContractDraft;
import com.chatmatchingservice.hqadmin.domain.draft.repository.EventDraftRepository;
import com.chatmatchingservice.hqadmin.domain.draft.repository.SalesContractDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class HqApprovalService {

    private final EventDraftRepository eventDraftRepository;
    private final SalesContractDraftRepository contractDraftRepository; // 추가된 레포지토리
    private final EventApprovalRepository approvalRepository;

    /**
     * 1. 판매 계약(Sales Contract) 승인
     */
    public ApprovalResponse approveContract(Long draftId, Long adminId) {
        // ticket_manager.sales_contract_draft 테이블에서 조회
        SalesContractDraft draft = contractDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계약 Draft입니다."));


        // 상태 검증: REQUESTED 상태만 승인 가능
        if (draft.getStatus() != DraftStatus.REQUESTED) {
            throw new IllegalStateException("REQUESTED 상태의 계약만 승인할 수 있습니다.");
        }

        // 상태 변경 (REQUESTED -> APPROVED)
        draft.approve();

        // 승인 로그 저장 (필요시)
        EventApprovalEntity approval = EventApprovalEntity.approve(draftId, adminId);
        approvalRepository.save(approval);

        return new ApprovalResponse(
                draftId,
                ApprovalStatus.APPROVED,
                null,
                approval.getDecidedAt()
        );
    }

    /**
     * 2. 공연(Event) 승인 (기존 로직)
     */
    public ApprovalResponse approve(Long draftId, Long adminId) {
        EventDraftEntity draft = eventDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연 Draft입니다."));

        validateDraft(draft);

        EventApprovalEntity approval = EventApprovalEntity.approve(draftId, adminId);
        approvalRepository.save(approval);

        draft.approve();

        return new ApprovalResponse(
                draftId,
                ApprovalStatus.APPROVED,
                null,
                approval.getDecidedAt()
        );
    }

    public ApprovalResponse reject(Long draftId, Long adminId, ApprovalRequest request) {
        if (request.reason() == null || request.reason().isBlank()) {
            throw new IllegalArgumentException("반려 사유는 필수입니다.");
        }

        EventDraftEntity draft = eventDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Draft입니다."));

        validateDraft(draft);

        EventApprovalEntity approval = EventApprovalEntity.reject(draftId, adminId, request.reason());
        approvalRepository.save(approval);

        draft.reject();

        return new ApprovalResponse(
                draftId,
                ApprovalStatus.REJECTED,
                request.reason(),
                approval.getDecidedAt()
        );
    }

    private void validateDraft(EventDraftEntity draft) {
        if (draft.getStatus() != DraftStatus.REQUESTED) {
            throw new IllegalStateException("REQUESTED 상태의 Draft만 승인/반려할 수 있습니다.");
        }
        approvalRepository.findByEventDraftId(draft.getId())
                .ifPresent(a -> {
                    throw new IllegalStateException("이미 승인 처리된 Draft입니다.");
                });
    }
}