package com.chatmatchingservice.hqadmin.domain.approval.repository;


import com.chatmatchingservice.hqadmin.domain.approval.entity.EventApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventApprovalRepository
        extends JpaRepository<EventApprovalEntity, Long> {

    Optional<EventApprovalEntity> findByEventDraftId(Long eventDraftId);
}
