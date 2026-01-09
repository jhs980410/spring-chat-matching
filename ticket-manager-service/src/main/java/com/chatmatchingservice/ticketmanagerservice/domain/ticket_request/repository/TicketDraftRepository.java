package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.repository;

import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.entity.TicketDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketDraftRepository extends JpaRepository<TicketDraft, Long> {

    List<TicketDraft> findByEventDraftId(Long eventDraftId);




}
