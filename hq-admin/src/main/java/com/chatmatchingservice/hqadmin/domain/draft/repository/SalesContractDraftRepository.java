package com.chatmatchingservice.hqadmin.domain.draft.repository;

import com.chatmatchingservice.hqadmin.domain.draft.dto.SalesContractDraftRecord;
import com.chatmatchingservice.hqadmin.domain.draft.entity.SalesContractDraft; // 8082 엔티티 경로
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SalesContractDraftRepository extends JpaRepository<SalesContractDraft, Long> {
    // REQUESTED 상태인 것들을 Record 리스트로 바로 반환
    @Query("""
    SELECT new com.chatmatchingservice.hqadmin.domain.draft.dto.SalesContractDraftRecord(
        s.id,
        s.businessName,
        s.businessNumber,
        s.settlementEmail,
        s.issueMethod,
        s.status,
        s.requestedAt
    )
    FROM SalesContractDraft s
    WHERE s.status = 'REQUESTED'
    ORDER BY s.requestedAt DESC
""")
    List<SalesContractDraftRecord> findPendingContracts();
}