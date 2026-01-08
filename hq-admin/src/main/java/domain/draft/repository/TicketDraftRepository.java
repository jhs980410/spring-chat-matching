package domain.draft.repository;

import domain.draft.entity.TicketDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketDraftRepository
        extends JpaRepository<TicketDraftEntity, Long> {

    List<TicketDraftEntity> findByEventDraftId(Long eventDraftId);
}
