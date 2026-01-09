package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.service;

import com.chatmatchingservice.ticketmanagerservice.domain.contract.entity.SalesContractDraft;
import com.chatmatchingservice.ticketmanagerservice.domain.contract.repository.SalesContractDraftRepository;
import com.chatmatchingservice.ticketmanagerservice.domain.manager.entity.TicketManager;
import com.chatmatchingservice.ticketmanagerservice.domain.manager.repository.TicketManagerRepository;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.eventDraft.EventDraftCreateRequest;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.eventDraft.EventDraftDetailResponse;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.eventDraft.EventDraftResponse;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.ticketDraft.TicketDraftCreateRequest;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.ticketDraft.TicketDraftResponse;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.entity.DraftStatus;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.entity.EventDraft;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.entity.TicketDraft;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.repository.EventDraftRepository;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.repository.TicketDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class TicketRequestService {

    private final EventDraftRepository eventDraftRepository;
    private final TicketDraftRepository ticketDraftRepository;
    private final TicketManagerRepository ticketManagerRepository;
    private final SalesContractDraftRepository salesContractDraftRepository;

    public Long createDraft(
            Long managerId,
            Long contractDraftId,
            EventDraftCreateRequest eventReq,
            List<TicketDraftCreateRequest> ticketReqs
    ) {
        if (ticketReqs == null || ticketReqs.isEmpty()) {
            throw new IllegalArgumentException("티켓은 최소 1개 이상 필요합니다.");
        }

        TicketManager manager = ticketManagerRepository.findById(managerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 매니저입니다.")
                );

        SalesContractDraft contractDraft =
                salesContractDraftRepository.findById(contractDraftId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("존재하지 않는 계약 Draft입니다.")
                        );

        if (!contractDraft.getManager().getId().equals(managerId)) {
            throw new IllegalStateException("계약 Draft 접근 권한이 없습니다.");
        }

        EventDraft eventDraft = EventDraft.create(
                manager,
                contractDraft,
                eventReq.domainId(),
                eventReq.title(),
                eventReq.description(),
                eventReq.venue(),
                eventReq.startAt(),
                eventReq.endAt(),
                eventReq.thumbnail()
        );

        eventDraftRepository.save(eventDraft);

        for (TicketDraftCreateRequest t : ticketReqs) {
            TicketDraft ticketDraft = TicketDraft.create(
                    eventDraft,
                    t.name(),
                    t.price(),
                    t.totalQuantity()
            );
            ticketDraftRepository.save(ticketDraft);
        }

        return eventDraft.getId();
    }


    public void requestApproval(Long draftId, Long managerId) {
        EventDraft draft = eventDraftRepository.findById(draftId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 Draft입니다.")
                );

        if (!draft.getManager().getId().equals(managerId)) {
            throw new IllegalStateException("해당 Draft에 대한 요청 권한이 없습니다.");
        }

        draft.request();
    }

    @Transactional(readOnly = true)
    public List<EventDraftResponse> getDrafts(Long managerId, DraftStatus status) {

        List<EventDraft> drafts = (status == null)
                ? eventDraftRepository.findByManager_Id(managerId)
                : eventDraftRepository.findByManager_IdAndStatus(managerId, status);

        return drafts.stream()
                .map(d -> new EventDraftResponse(
                        d.getId(),
                        d.getTitle(),
                        d.getStatus(),
                        d.getCreatedAt(),
                        d.getRequestedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public EventDraftDetailResponse getDraftDetail(Long draftId, Long managerId) {

        EventDraft draft = eventDraftRepository.findById(draftId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 Draft입니다.")
                );

        if (!draft.getManager().getId().equals(managerId)) {
            throw new IllegalStateException("조회 권한이 없습니다.");
        }

        List<TicketDraftResponse> tickets =
                ticketDraftRepository.findByEventDraftId(draftId)
                        .stream()
                        .map(t -> new TicketDraftResponse(
                                t.getId(),
                                t.getName(),
                                t.getPrice(),
                                t.getTotalQuantity()
                        ))
                        .toList();

        return new EventDraftDetailResponse(
                draft.getId(),
                draft.getTitle(),
                draft.getDescription(),
                draft.getVenue(),
                draft.getStartAt(),
                draft.getEndAt(),
                draft.getThumbnail(),
                draft.getStatus(),
                draft.getCreatedAt(),
                draft.getRequestedAt(),
                tickets
        );
    }


}
