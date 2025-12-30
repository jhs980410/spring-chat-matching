package com.chatmatchingservice.springchatmatching.domain.ticket.controller;

import com.chatmatchingservice.springchatmatching.domain.event.dto.SectionResponseDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.service.SeatQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Seat",
        description = """
    좌석 조회 API

    - 이벤트별 좌석 구역 및 좌석 목록 조회
    - 예매 전 좌석 현황 확인용 조회 API
    - 좌석 상태(AVAILABLE / LOCKED / SOLD) 반영
    """
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class SeatController {

    private final SeatQueryService seatQueryService;

    // ========================================
    // 좌석 조회
    // ========================================
    @Operation(
            summary = "이벤트 좌석 조회",
            description = """
        특정 이벤트의 좌석 정보를 조회하는 API

        - 좌석 구역(Section) 단위로 조회
        - 각 좌석의 현재 상태 포함
        """
    )
    @GetMapping("/{eventId}/seats")
    public List<SectionResponseDto> getSeats(@PathVariable Long eventId) {
        return seatQueryService.getSectionsWithSeats(eventId);
    }
}
