package com.chatmatchingservice.hqadmin.domain.draft.repository;

import com.chatmatchingservice.hqadmin.domain.draft.entity.SalesContractDraft; // 8082 엔티티 경로
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface SalesContractDraftRepository extends JpaRepository<SalesContractDraft, Long> {

}