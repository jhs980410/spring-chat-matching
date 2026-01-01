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
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j // 로그 확인용 추가
public class EventService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final RedisRepository redisRepository; //  RedisRepository 주입 추가

    public EventDetailDto getEventDetail(Long eventId) {
        // 상세 페이지도 나중에 필요하면 캐싱할 수 있지만, 우선순위는 홈입니다.
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        List<TicketOptionDto> tickets = ticketRepository.findByEventId(eventId)
                .stream()
                .map(TicketOptionDto::from)
                .toList();

        return EventDetailDto.from(event, tickets);
    }

    /**
     * 캐시가 적용된 홈 조회 로직
     */
    public HomeResponseDto getHome() {
        // 1. 캐시 확인 (Cache-Aside 전략)
        HomeResponseDto cachedData = redisRepository.getHomeCache();
        if (cachedData != null) {
            log.info("🎯 [Redis] Home Cache Hit! DB를 조회하지 않고 응답합니다.");
            return cachedData;
        }

        log.info("☁️ [DB] Home Cache Miss! DB에서 데이터를 새로 구성합니다.");

        // 2. 캐시 없으면 기존 무거운 로직 수행
        HomeResponseDto homeResponse = buildHomeResponse();

        // 3. 캐시에 저장 (10분 TTL 권장)
        redisRepository.setHomeCache(homeResponse, 10);

        return homeResponse;
    }

    /**
     * 기존 getHome 로직을 메서드로 추출하여 가독성 확보
     */
    private HomeResponseDto buildHomeResponse() {
        // 1) Hero Banner
        List<HeroBannerDto> heroBanners = List.of(
                HeroBannerDto.of(1L, "인기 공연 최대 할인", "놓치면 끝", "/images/banner1.jpg"),
                HeroBannerDto.of(2L, "연말 콘서트 오픈", "지금 예매하세요", "/images/banner2.jpg")
        );

        // 2) Featured Events
        List<EventSummaryDto> featuredEvents =
                eventRepository.findTop5ByStatusOrderByCreatedAtDesc(EventStatus.OPEN)
                        .stream()
                        .map(EventSummaryDto::from)
                        .toList();

        // 3) Category Rankings (가장 부하가 높은 지점)
        List<EventCategory> categories = eventCategoryRepository.findAll();
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