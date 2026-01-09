package com.chatmatchingservice.ticketmanagerservice.domain.contract.repository;

import com.chatmatchingservice.ticketmanagerservice.domain.contract.entity.SalesContractDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesContractDraftRepository
        extends JpaRepository<SalesContractDraft, Long> {

    List<SalesContractDraft> findByManager_Id(Long managerId);
}
