package com.chatmatchingservice.springchatmatching.domain.ticket.service;

import com.chatmatchingservice.springchatmatching.domain.ticket.dto.*;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Event;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.EventCategory;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.EventStatus;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.EventRepository;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public EventDetailDto getEventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        List<TicketOptionDto> tickets = ticketRepository.findByEventId(eventId)
                .stream()
                .map(TicketOptionDto::from)
                .toList();

        return EventDetailDto.from(event, tickets);
    }


    public HomeResponseDto getHome() {

        // 1️⃣ Hero Banner (지금은 이벤트 일부 재사용 or 고정값)
        List<HeroBannerDto> heroBanners = List.of(
                HeroBannerDto.of(1L, "인기 공연 최대 할인", "놓치면 끝", "/images/banner1.jpg"),
                HeroBannerDto.of(2L, "연말 콘서트 오픈", "지금 예매하세요", "/images/banner2.jpg")
        );

        // 2️⃣ Featured Events (OPEN 중 최신)
        List<EventSummaryDto> featuredEvents =
                eventRepository.findTop5ByStatusOrderByCreatedAtDesc(EventStatus.OPEN)
                        .stream()
                        .map(EventSummaryDto::from)
                        .toList();

        // 3️⃣ Category Rankings
        // 3️⃣ Category Rankings
        Map<EventCategory, List<EventSummaryDto>> rankings = new EnumMap<>(EventCategory.class);

        for (EventCategory category : EventCategory.values()) {

            List<Event> events =
                    eventRepository.findTop10ByCategoryAndStatusOrderByCreatedAtDesc(
                            category, EventStatus.OPEN
                    );

            List<EventSummaryDto> list = new java.util.ArrayList<>();

            int rank = 1;
            for (Event event : events) {
                list.add(EventSummaryDto.fromWithRanking(event, rank++));
            }

            rankings.put(category, list);
        }
        // 4️⃣ Open Soon
        List<EventSummaryDto> openSoonEvents =
                eventRepository.findTop5ByStartAtAfterOrderByStartAtAsc(LocalDateTime.now())
                        .stream()
                        .map(EventSummaryDto::fromOpenSoon)
                        .toList();

        return HomeResponseDto.builder()
                .heroBanners(heroBanners)
                .featuredEvents(featuredEvents)
                .rankings(rankings)
                .openSoonEvents(openSoonEvents)
                .build();
    }

}
