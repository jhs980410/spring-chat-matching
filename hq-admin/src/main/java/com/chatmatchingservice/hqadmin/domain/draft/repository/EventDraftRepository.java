package com.chatmatchingservice.hqadmin.domain.draft.repository;

import com.chatmatchingservice.hqadmin.domain.draft.entity.DraftStatus;
import com.chatmatchingservice.hqadmin.domain.draft.entity.EventDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional; // 이 import가 필요합니다.

public interface EventDraftRepository extends JpaRepository<EventDraftEntity, Long> {

    List<EventDraftEntity> findByStatus(DraftStatus status);

    // DB의 sales_contract_draft_id 컬럼을 기준으로 조회
    Optional<EventDraftEntity> findBySalesContractDraftId(Long salesContractDraftId);
}