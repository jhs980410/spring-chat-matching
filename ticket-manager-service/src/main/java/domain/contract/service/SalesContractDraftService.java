package domain.contract.service;

import domain.contract.dto.SalesContractDraftCreateRequest;
import domain.contract.dto.SalesContractDraftResponse;
import domain.contract.entity.SalesContractDraft;
import domain.manager.entity.TicketManager;
import domain.manager.repository.TicketManagerRepository;
import domain.contract.repository.SalesContractDraftRepository;
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
                req.businessName(),
                req.businessNumber(),
                req.ceoName(),
                req.contactEmail(),
                req.contactPhone()
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
