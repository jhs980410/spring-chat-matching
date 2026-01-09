package domain.draft.query.service;

import domain.draft.entity.DraftStatus;
import domain.draft.entity.EventDraftEntity;
import domain.draft.query.dto.EventDraftDetailResponse;
import domain.draft.query.dto.EventDraftSummaryResponse;
import domain.draft.query.dto.TicketDraftResponse;
import domain.draft.repository.EventDraftRepository;
import domain.draft.repository.TicketDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HqDraftQueryService {

    private final EventDraftRepository eventDraftRepository;
    private final TicketDraftRepository ticketDraftRepository;

    public List<EventDraftSummaryResponse> getRequestedDrafts() {
        return eventDraftRepository.findByStatus(DraftStatus.REQUESTED)
                .stream()
                .map(d -> new EventDraftSummaryResponse(
                        d.getId(),
                        d.getTitle(),
                        d.getStatus(),
                        d.getRequestedAt(),
                        d.getCreatedAt()
                ))
                .toList();
    }

    public EventDraftDetailResponse getDraftDetail(Long draftId) {
        EventDraftEntity draft = eventDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Draft not found"));

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
                draft.getManagerId(),
                draft.getDomainId(),
                draft.getTitle(),
                draft.getDescription(),
                draft.getVenue(),
                draft.getStartAt(),
                draft.getEndAt(),
                draft.getThumbnail(),
                draft.getStatus(),
                draft.getRequestedAt(),
                draft.getCreatedAt(),
                tickets
        );
    }
}
