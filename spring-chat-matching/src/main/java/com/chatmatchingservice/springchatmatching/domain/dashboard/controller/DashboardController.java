package com.chatmatchingservice.springchatmatching.domain.dashboard.controller;

import com.chatmatchingservice.springchatmatching.domain.dashboard.dto.*;
import com.chatmatchingservice.springchatmatching.domain.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Dashboard",
        description = """
    운영 대시보드 조회 API

    - 상담 서비스 운영 지표(KPI) 조회
    - 상담 세션 상태 비율 조회
    - 당일 상담 세션 현황 조회
    """
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // ========================================
    // KPI 조회
    // ========================================
    @Operation(
            summary = "대시보드 KPI 조회",
            description = "상담 서비스 운영 현황을 요약한 핵심 KPI 지표 조회"
    )
    @GetMapping("/kpis")
    public DashboardKpiResponse getKpis() {
        return dashboardService.getKpis();
    }

    // ========================================
    // 세션 상태 비율
    // ========================================
    @Operation(
            summary = "상담 세션 상태 비율 조회",
            description = "상담 세션 상태별 비율 통계 조회"
    )
    @GetMapping("/status-ratio")
    public DashboardStatusRatioResponse getStatusRatio() {
        return dashboardService.getStatusRatio();
    }

    // ========================================
    // 오늘의 상담 세션
    // ========================================
    @Operation(
            summary = "오늘의 상담 세션 조회",
            description = "금일 생성·진행된 상담 세션 목록 조회"
    )
    @GetMapping("/sessions/today")
    public List<TodaySessionResponse> getTodaySessions() {
        return dashboardService.getTodaySessions();
    }
}
