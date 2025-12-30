package com.chatmatchingservice.springchatmatching.domain.stats.controller;

import com.chatmatchingservice.springchatmatching.domain.stats.dto.CounselorHandledResponse;
import com.chatmatchingservice.springchatmatching.domain.stats.dto.DailyStatsResponse;
import com.chatmatchingservice.springchatmatching.domain.stats.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Statistics",
        description = """
    상담 서비스 통계 조회 API

    - 일별 상담 통계 조회
    - 상담사 처리 건수 통계 조회
    - 운영/분석 목적의 집계 데이터 제공
    """
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    // ========================================
    // 일별 통계 조회
    // ========================================
    @Operation(
            summary = "일별 상담 통계 조회",
            description = "날짜 기준으로 집계된 상담 서비스 일별 통계 데이터 조회"
    )
    @GetMapping("/daily")
    public List<DailyStatsResponse> getDailyStats() {
        return statsService.getDailyStats();
    }

    // ========================================
    // 상담사 처리 건수 통계
    // ========================================
    @Operation(
            summary = "상담사 처리 건수 통계 조회",
            description = "상담사별 처리한 상담 세션 건수 통계 조회"
    )
    @GetMapping("/counselors/handled")
    public List<CounselorHandledResponse> getCounselorHandled() {
        return statsService.getCounselorHandled();
    }
}
