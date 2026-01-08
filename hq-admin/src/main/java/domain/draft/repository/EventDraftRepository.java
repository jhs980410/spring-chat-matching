package domain.draft.repository;

import domain.draft.entity.EventDraftEntity;
import domain.draft.entity.DraftStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventDraftRepository
        extends JpaRepository<EventDraftEntity, Long> {

    List<EventDraftEntity> findByStatus(DraftStatus status);
}