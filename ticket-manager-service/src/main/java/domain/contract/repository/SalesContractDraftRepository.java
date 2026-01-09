package domain.contract.repository;

import domain.contract.entity.SalesContractDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesContractDraftRepository
        extends JpaRepository<SalesContractDraft, Long> {

    List<SalesContractDraft> findByManager_Id(Long managerId);
}
