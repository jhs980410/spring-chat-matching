package com.chatmatchingservice.hqadmin.domain.approval.repository;


import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalResponse;
import com.chatmatchingservice.hqadmin.domain.approval.entity.EventApprovalEntity;
import com.chatmatchingservice.hqadmin.domain.draft.entity.DraftStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventApprovalRepository
        extends JpaRepository<EventApprovalEntity, Long> {
    Optional<EventApprovalEntity> findByEventDraftId(Long eventDraftId);
}
