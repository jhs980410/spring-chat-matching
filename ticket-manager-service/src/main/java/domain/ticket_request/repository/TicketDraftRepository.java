package domain.ticket_request.repository;

import domain.ticket_request.entity.DraftStatus;
import domain.ticket_request.entity.EventDraft;
import domain.ticket_request.entity.TicketDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketDraftRepository extends JpaRepository<TicketDraft, Long> {

    List<TicketDraft> findByEventDraftId(Long eventDraftId);




}
