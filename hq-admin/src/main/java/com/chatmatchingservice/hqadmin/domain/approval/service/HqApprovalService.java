package com.chatmatchingservice.hqadmin.domain.approval.service;

import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalListResponse;
import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalRequest;
import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalResponse;
import com.chatmatchingservice.hqadmin.domain.approval.entity.ApprovalStatus;
import com.chatmatchingservice.hqadmin.domain.approval.entity.EventApprovalEntity;
import com.chatmatchingservice.hqadmin.domain.approval.repository.EventApprovalRepository;
import com.chatmatchingservice.hqadmin.domain.draft.dto.SalesContractDraftRecord;
import com.chatmatchingservice.hqadmin.domain.draft.entity.DraftStatus;
import com.chatmatchingservice.hqadmin.domain.draft.entity.EventDraftEntity;
import com.chatmatchingservice.hqadmin.domain.draft.entity.SalesContractDraft;
import com.chatmatchingservice.hqadmin.domain.draft.repository.EventDraftRepository;
import com.chatmatchingservice.hqadmin.domain.draft.repository.SalesContractDraftRepository;
import com.chatmatchingservice.hqadmin.domain.publish.repository.EventPublishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HqApprovalService {

    private final EventDraftRepository eventDraftRepository;
    private final SalesContractDraftRepository contractDraftRepository; // 추가된 레포지토리
    private final EventApprovalRepository approvalRepository;
    private final EventPublishRepository eventPublishRepository;

    @Transactional(readOnly = true)
    public List<SalesContractDraftRecord> getPendingContracts() {

        return contractDraftRepository.findPendingContracts();
    }



    /**
     * 1. 판매 계약(Sales Contract) 승인
     */
    public ApprovalResponse approveContract(Long draftId, Long adminId) {
        SalesContractDraft draft = contractDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계약 Draft입니다."));

        if (draft.getStatus() != DraftStatus.REQUESTED) {
            throw new IllegalStateException("REQUESTED 상태의 계약만 승인할 수 있습니다.");
        }

        draft.approve(); // sales_contract_draft 테이블만 APPROVED로 변경

        // EventApprovalEntity approval = EventApprovalEntity.approve(draftId, adminId);
        // approvalRepository.save(approval);

        return new ApprovalResponse(
                draftId,
                ApprovalStatus.APPROVED,
                null,
                LocalDateTime.now() // 결정 시간은 현재 시간으로 대체
        );
    }
    /**
     * 2. 공연(Event) 승인 (기존 로직)
     */
    public ApprovalResponse approve(Long draftId, Long adminId) {
        EventDraftEntity draft = eventDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연 Draft입니다."));

        validateDraft(draft);

        // 1. 이력 저장
        EventApprovalEntity approval = EventApprovalEntity.approve(draftId, adminId);
        approvalRepository.save(approval);

        // 2. 상태 변경 및 명시적 저장
        draft.approve(); // 내부적으로 this.status = DraftStatus.APPROVED; 수행
        eventDraftRepository.save(draft); //  이 코드를 넣어 UPDATE를 강제

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

    @Transactional(readOnly = true)
    public List<ApprovalListResponse> getRequestedDrafts() {
        // 1. Draft 테이블에서 REQUESTED 상태인 목록 조회
        List<EventDraftEntity> requestedDrafts =
                eventDraftRepository.findAllByStatusOrderByCreatedAtDesc(DraftStatus.REQUESTED);

        // 2. 목록용 DTO(ApprovalListResponse)로 변환
        return requestedDrafts.stream()
                .map(draft -> ApprovalListResponse.builder()
                        .eventDraftId(draft.getId())
                        .title(draft.getTitle()) // Draft 엔티티의 제목 필드 활용
                        .status(draft.getStatus().name()) // Enum -> String 변환으로 에러 해결
                        .requestedAt(draft.getCreatedAt())
                        .build())
                .toList();
    }
    @Transactional(readOnly = true)
    public List<ApprovalListResponse> getApprovedDrafts() {
        // 1. APPROVED 상태인 Draft만 조회 [cite: 2026-01-13]
        List<EventDraftEntity> approvedDrafts =
                eventDraftRepository.findAllByStatusOrderByCreatedAtDesc(DraftStatus.APPROVED);

        // 2. 이미 발행된(Publish) 내역이 없는 것만 필터링하여 반환 [cite: 2026-01-13]
        return approvedDrafts.stream()
                .filter(draft -> eventPublishRepository.findByEventDraftId(draft.getId()).isEmpty())
                .map(draft -> ApprovalListResponse.builder()
                        .eventDraftId(draft.getId())
                        .title(draft.getTitle())
                        .status(draft.getStatus().name())
                        .requestedAt(draft.getCreatedAt())
                        .build())
                .toList();
    }

}