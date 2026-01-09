package com.chatmatchingservice.hqadmin.domain.publish.service;


import com.chatmatchingservice.hqadmin.domain.approval.repository.EventApprovalRepository;
import com.chatmatchingservice.hqadmin.domain.draft.entity.DraftStatus;
import com.chatmatchingservice.hqadmin.domain.draft.entity.EventDraftEntity;
import com.chatmatchingservice.hqadmin.domain.draft.repository.EventDraftRepository;
import com.chatmatchingservice.hqadmin.domain.draft.repository.TicketDraftRepository;
import com.chatmatchingservice.hqadmin.domain.publish.entity.Event;
import com.chatmatchingservice.hqadmin.domain.publish.entity.EventPublishEntity;
import com.chatmatchingservice.hqadmin.domain.publish.entity.Ticket;
import com.chatmatchingservice.hqadmin.domain.publish.repository.EventPublishRepository;
import com.chatmatchingservice.hqadmin.domain.publish.repository.EventRepository;
import com.chatmatchingservice.hqadmin.domain.publish.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventPublishService {

    private final EventDraftRepository eventDraftRepository;
    private final TicketDraftRepository ticketDraftRepository;
    private final EventApprovalRepository approvalRepository;
    private final EventPublishRepository publishRepository;

    // 운영 DB Repository (8080 스키마)
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public Long publish(Long draftId) {

        EventDraftEntity draft = eventDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Draft입니다."));

        if (draft.getStatus() != DraftStatus.APPROVED) {
            throw new IllegalStateException("APPROVED 상태의 Draft만 publish 가능합니다.");
        }

        approvalRepository.findByEventDraftId(draftId)
                .orElseThrow(() -> new IllegalStateException("승인 이력이 존재하지 않습니다."));

        publishRepository.findByEventDraftId(draftId)
                .ifPresent(p -> {
                    throw new IllegalStateException("이미 publish된 Draft입니다.");
                });

        Event event = Event.create(
                draft.getDomainId(),
                draft.getTitle(),
                draft.getDescription(),
                draft.getVenue(),
                draft.getStartAt(),
                draft.getEndAt(),
                draft.getThumbnail()
        );

        eventRepository.save(event);

        ticketDraftRepository.findByEventDraftId(draftId)
                .forEach(td -> ticketRepository.save(
                        Ticket.create(event, td.getName(), td.getPrice(), td.getTotalQuantity())
                ));

        publishRepository.save(
                EventPublishEntity.of(draftId, event.getId())
        );

        return event.getId();
    }

}
