package com.chatmatchingservice.hqadmin.domain.approval.repository;

import com.chatmatchingservice.hqadmin.domain.approval.entity.ContractApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractApprovalRepository extends JpaRepository<ContractApprovalEntity, Long> {

    //계약 초안 ID로 승인 이력을 찾기
    Optional<ContractApprovalEntity> findBySalesContractDraftId(Long draftId);

}