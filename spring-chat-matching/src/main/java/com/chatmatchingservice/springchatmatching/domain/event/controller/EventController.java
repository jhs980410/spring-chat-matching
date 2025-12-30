package com.chatmatchingservice.springchatmatching.domain.event.controller;

import com.chatmatchingservice.springchatmatching.domain.event.dto.EventDetailDto;
import com.chatmatchingservice.springchatmatching.domain.mypage.dto.HomeResponseDto;
import com.chatmatchingservice.springchatmatching.domain.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Event",
        description = """
    티켓 이벤트 조회 API

    - 홈 화면 구성용 이벤트 데이터 제공
    - 이벤트 상세 및 티켓 옵션 조회
    - 티켓 예매 전 단계의 조회 전용 API
    """
)
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // ========================================
    // 홈 화면용 이벤트 데이터
    // ========================================
    @Operation(
            summary = "이벤트 홈 데이터 조회",
            description = """
        티켓 서비스 홈 화면에 사용되는 데이터 조회

        - Hero Banner
        - 추천 이벤트
        - 카테고리별 랭킹
        - 오픈 예정 이벤트
        """
    )
    @GetMapping("/home")
    public HomeResponseDto getHome() {
        return eventService.getHome();
    }

    // ========================================
    // 이벤트 상세 조회
    // ========================================
    @Operation(
            summary = "이벤트 상세 조회",
            description = """
        특정 이벤트의 상세 정보를 조회하는 API

        - 이벤트 기본 정보
        - 티켓 옵션 목록
        """
    )
    @GetMapping("/{eventId}")
    public EventDetailDto getEventDetail(@PathVariable Long eventId) {
        return eventService.getEventDetail(eventId);
    }
}
