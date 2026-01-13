package com.chatmatchingservice.ticketmanagerservice.domain.contract.service;

import com.chatmatchingservice.ticketmanagerservice.domain.contract.dto.SalesContractDraftCreateRequest;
import com.chatmatchingservice.ticketmanagerservice.domain.contract.dto.SalesContractDraftResponse;
import com.chatmatchingservice.ticketmanagerservice.domain.contract.entity.SalesContractDraft;
import com.chatmatchingservice.ticketmanagerservice.domain.manager.entity.TicketManager;
import com.chatmatchingservice.ticketmanagerservice.domain.manager.repository.TicketManagerRepository;
import com.chatmatchingservice.ticketmanagerservice.domain.contract.repository.SalesContractDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesContractDraftService {

    private final SalesContractDraftRepository repository;
    private final TicketManagerRepository managerRepository;

    public Long create(Long managerId, SalesContractDraftCreateRequest req) {

        TicketManager manager = managerRepository.findById(managerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 매니저입니다.")
                );

        SalesContractDraft draft = SalesContractDraft.create(
                manager,
                req.partnerDraftId(),
                req.domainId(),
                req.businessName(),
                req.businessNumber(),
                req.ceoName(),
                req.contactEmail(),
                req.contactPhone(),
                req.settlementEmail(),
                req.salesReportEmail(),
                req.taxEmail(),
                req.issueMethod()
        );

        repository.save(draft);
        return draft.getId();
    }

    public void request(Long draftId, Long managerId) {
        SalesContractDraft draft = repository.findById(draftId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 계약 Draft입니다.")
                );

        if (!draft.getManager().getId().equals(managerId)) {
            throw new IllegalStateException("요청 권한이 없습니다.");
        }

        draft.request();
    }

    @Transactional(readOnly = true)
    public List<SalesContractDraftResponse> getMyDrafts(Long managerId) {
        return repository.findByManager_Id(managerId)
                .stream()
                .map(d -> new SalesContractDraftResponse(
                        d.getId(),
                        d.getBusinessName(),
                        d.getStatus(),
                        d.getCreatedAt(),
                        d.getRequestedAt()
                ))
                .toList();
    }
}
