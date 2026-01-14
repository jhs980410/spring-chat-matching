package com.chatmatchingservice.hqadmin.domain.publish.service;

import com.chatmatchingservice.hqadmin.domain.approval.repository.EventApprovalRepository;
import com.chatmatchingservice.hqadmin.domain.draft.entity.DraftStatus;
import com.chatmatchingservice.hqadmin.domain.draft.entity.EventDraftEntity;
import com.chatmatchingservice.hqadmin.domain.draft.repository.EventDraftRepository;
import com.chatmatchingservice.hqadmin.domain.draft.repository.TicketDraftRepository;
import com.chatmatchingservice.hqadmin.domain.publish.entity.*;
import com.chatmatchingservice.hqadmin.domain.publish.repository.*;
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

    // 운영 DB Repository (chatmaching 스키마)
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final VenueSectionRepository venueSectionRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Long publish(Long draftId) {
        // 1. Draft 및 승인 상태 검증
        EventDraftEntity draft =eventDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("ID: " + draftId + " 에 해당하는 Draft를 찾을 수 없습니다."));
        if (draft.getStatus() != DraftStatus.APPROVED) {
            throw new IllegalStateException("APPROVED 상태의 Draft만 publish 가능합니다.");
        }

        approvalRepository.findByEventDraftId(draftId)
                .orElseThrow(() -> new IllegalStateException("승인 이력이 존재하지 않습니다."));

        publishRepository.findByEventDraftId(draftId)
                .ifPresent(p -> {
                    throw new IllegalStateException("이미 publish된 Draft입니다.");
                });

        // 2. 운영 Event 생성 및 저장 (ID 확보를 위해 리턴값 받음)
        Event savedEvent = eventRepository.save(
                Event.create(
                        draft.getDomainId(),
                        draft.getTitle(),
                        draft.getDescription(),
                        draft.getVenue(),
                        draft.getStartAt(),
                        draft.getEndAt(),
                        draft.getThumbnail()
                )
        );

        // 3. 티켓 초안(TicketDraft)을 순회하며 데이터 생성
        ticketDraftRepository.findByEventDraftId(draftId).forEach(td -> {

            // (1) 운영 Ticket 생성 및 저장 (ID가 할당된 savedTicket 객체 확보)
            Ticket savedTicket = ticketRepository.save(
                    Ticket.create(
                            savedEvent,
                            td.getName(),
                            td.getPrice(),
                            td.getTotalQuantity()
                    )
            );

            // (2) 운영 VenueSection 생성 및 저장 (ID가 할당된 savedSection 객체 확보)
            // 위에서 저장된 savedEvent와 savedTicket을 인자로 넘깁니다.
            VenueSection savedSection = venueSectionRepository.save(
                    VenueSection.create(
                            savedEvent,
                            savedTicket,
                            draft.getVenue(),    // 공연장 명칭
                            td.getSectionCode(),  // 구역 코드
                            td.getSectionName(),  // 구역 명칭
                            "NORMAL"             // 등급
                    )
            );

            // (3) 운영 Seat 생성 (물리적 좌석 확보)
            // 위에서 저장된 savedSection을 부모로 하여 좌석들을 생성합니다.
            for (int i = 1; i <= td.getTotalQuantity(); i++) {
                Seat seat = Seat.create(
                        savedSection,
                        td.getRowLabel(), // 행 정보 (예: A열)
                        i                // 좌석 번호 (1, 2, 3...)
                );
                seatRepository.save(seat);
            }
        });

        // 4. Publish 이력 저장
        publishRepository.save(
                EventPublishEntity.of(draftId, savedEvent.getId())
        );

        return savedEvent.getId();
    }
}