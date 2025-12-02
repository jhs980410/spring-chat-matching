package com.chatmatchingservice.springchatmatching.domain.stats.controller;

import com.chatmatchingservice.springchatmatching.domain.stats.dto.CounselorHandledResponse;
import com.chatmatchingservice.springchatmatching.domain.stats.dto.DailyStatsResponse;
import com.chatmatchingservice.springchatmatching.domain.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/daily")
    public List<DailyStatsResponse> getDailyStats() {
        return statsService.getDailyStats();
    }

    @GetMapping("/counselors/handled")
    public List<CounselorHandledResponse> getCounselorHandled() {
        return statsService.getCounselorHandled();
    }
}
