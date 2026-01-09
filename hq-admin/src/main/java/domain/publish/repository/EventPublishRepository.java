package domain.publish.repository;

import domain.publish.entity.EventPublishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventPublishRepository
        extends JpaRepository<EventPublishEntity, Long> {

    Optional<EventPublishEntity> findByEventDraftId(Long draftId);
}
