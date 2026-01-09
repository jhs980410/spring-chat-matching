package com.chatmatchingservice.hqadmin.domain.draft.repository;


import com.chatmatchingservice.hqadmin.domain.draft.entity.DraftStatus;
import com.chatmatchingservice.hqadmin.domain.draft.entity.EventDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventDraftRepository
        extends JpaRepository<EventDraftEntity, Long> {

    List<EventDraftEntity> findByStatus(DraftStatus status);
}