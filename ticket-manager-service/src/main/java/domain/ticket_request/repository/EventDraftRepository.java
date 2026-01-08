package domain.ticket_request.repository;

import domain.ticket_request.entity.DraftStatus;
import domain.ticket_request.entity.EventDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventDraftRepository extends JpaRepository<EventDraft, Long> {


    // 특정 매니저의 Draft 목록
    List<EventDraft> findByManager_Id(Long managerId);
    //특정매니저의 상태별조회및 id
    List<EventDraft> findByManager_IdAndStatus(Long managerId, DraftStatus status);

}
