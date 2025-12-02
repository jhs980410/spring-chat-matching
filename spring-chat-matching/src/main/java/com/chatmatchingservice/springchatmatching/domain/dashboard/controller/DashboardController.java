package com.chatmatchingservice.springchatmatching.domain.dashboard.controller;

import com.chatmatchingservice.springchatmatching.domain.dashboard.dto.*;
import com.chatmatchingservice.springchatmatching.domain.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/kpis")
    public DashboardKpiResponse getKpis() {
        return dashboardService.getKpis();
    }

    @GetMapping("/status-ratio")
    public DashboardStatusRatioResponse getStatusRatio() {
        return dashboardService.getStatusRatio();
    }

    @GetMapping("/sessions/today")
    public List<TodaySessionResponse> getTodaySessions() {
        return dashboardService.getTodaySessions();
    }
}
