package com.chatmatchingservice.springchatmatching.domain.event.service;

import com.chatmatchingservice.springchatmatching.domain.event.dto.EventDetailDto;
import com.chatmatchingservice.springchatmatching.domain.event.dto.EventSummaryDto;
import com.chatmatchingservice.springchatmatching.domain.mypage.dto.HeroBannerDto;
import com.chatmatchingservice.springchatmatching.domain.mypage.dto.HomeResponseDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.dto.*;
import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
import com.chatmatchingservice.springchatmatching.domain.event.entity.EventCategory;
import com.chatmatchingservice.springchatmatching.domain.event.entity.EventStatus;
import com.chatmatchingservice.springchatmatching.domain.event.repository.EventCategoryRepository;
import com.chatmatchingservice.springchatmatching.domain.event.repository.EventRepository;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final EventCategoryRepository eventCategoryRepository;

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

        // 1) Hero Banner (더미)
        List<HeroBannerDto> heroBanners = List.of(
                HeroBannerDto.of(1L, "인기 공연 최대 할인", "놓치면 끝", "/images/banner1.jpg"),
                HeroBannerDto.of(2L, "연말 콘서트 오픈", "지금 예매하세요", "/images/banner2.jpg")
        );

        // 2) Featured Events (OPEN 중 최신)
        List<EventSummaryDto> featuredEvents =
                eventRepository.findTop5ByStatusOrderByCreatedAtDesc(EventStatus.OPEN)
                        .stream()
                        .map(EventSummaryDto::from)
                        .toList();

        // 3) Category Rankings (DB 카테고리 기반)
        List<EventCategory> categories = eventCategoryRepository.findAll();

        // JSON 키로 엔티티 쓰지 말고 code(String)로 내려라
        Map<String, List<EventSummaryDto>> rankings = new LinkedHashMap<>();

        for (EventCategory category : categories) {
            List<Event> events =
                    eventRepository.findTop10ByCategoryAndStatusOrderByCreatedAtDesc(
                            category, EventStatus.OPEN
                    );

            List<EventSummaryDto> list = new ArrayList<>();
            int rank = 1;
            for (Event event : events) {
                list.add(EventSummaryDto.fromWithRanking(event, rank++));
            }

            rankings.put(category.getCode(), list);
        }

        // 4) Open Soon
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
