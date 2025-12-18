package com.chatmatchingservice.springchatmatching.domain.ticket.controller;

import com.chatmatchingservice.springchatmatching.domain.ticket.dto.EventDetailDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.dto.HomeResponseDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * 홈 화면용 데이터
     * - hero banners
     * - featured events
     * - rankings
     * - open soon
     */
    @GetMapping("/home")
    public HomeResponseDto getHome() {
        return eventService.getHome();
    }

    /**
     * 이벤트 상세 조회
     * - 이벤트 정보
     * - 티켓 옵션
     */
    @GetMapping("/{eventId}")
    public EventDetailDto getEventDetail(@PathVariable Long eventId) {
        return eventService.getEventDetail(eventId);
    }
}
