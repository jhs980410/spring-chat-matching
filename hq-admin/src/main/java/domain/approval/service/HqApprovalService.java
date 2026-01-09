package domain.approval.service;

import domain.approval.dto.ApprovalRequest;
import domain.approval.dto.ApprovalResponse;
import domain.approval.entity.ApprovalStatus;
import domain.approval.entity.EventApprovalEntity;
import domain.approval.repository.EventApprovalRepository;
import domain.draft.entity.DraftStatus;
import domain.draft.entity.EventDraftEntity;
import domain.draft.repository.EventDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HqApprovalService {

    private final EventDraftRepository eventDraftRepository;
    private final EventApprovalRepository approvalRepository;

    public ApprovalResponse approve(
            Long draftId,
            Long adminId
    ) {
        EventDraftEntity draft = eventDraftRepository.findById(draftId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 Draft입니다.")
                );

        validateDraft(draft);

        EventApprovalEntity approval =
                EventApprovalEntity.approve(draftId, adminId);

        approvalRepository.save(approval);

        // Draft 상태 변경 (REQUESTED → APPROVED)
        draft.approve();

        return new ApprovalResponse(
                draftId,
                ApprovalStatus.APPROVED,
                null,
                approval.getDecidedAt()
        );
    }

    public ApprovalResponse reject(
            Long draftId,
            Long adminId,
            ApprovalRequest request
    ) {
        if (request.reason() == null || request.reason().isBlank()) {
            throw new IllegalArgumentException("반려 사유는 필수입니다.");
        }

        EventDraftEntity draft = eventDraftRepository.findById(draftId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 Draft입니다.")
                );

        validateDraft(draft);

        EventApprovalEntity approval =
                EventApprovalEntity.reject(
                        draftId,
                        adminId,
                        request.reason()
                );

        approvalRepository.save(approval);

        // Draft 상태 변경 (REQUESTED → REJECTED)
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
            throw new IllegalStateException(
                    "REQUESTED 상태의 Draft만 승인/반려할 수 있습니다."
            );
        }

        approvalRepository.findByEventDraftId(draft.getId())
                .ifPresent(a -> {
                    throw new IllegalStateException("이미 승인 처리된 Draft입니다.");
                });
    }
}
