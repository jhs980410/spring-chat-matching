package com.chatmatchingservice.springchatmatching.domain.dashboard.service;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionJpaRepository;
import com.chatmatchingservice.springchatmatching.domain.dashboard.dto.*;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ChatSessionJpaRepository chatSessionJpaRepository;
    private final RedisRepository redisRepository;

    // ======================================================
    // 1. KPI 조회
    // ======================================================
    public DashboardKpiResponse getKpis() {

        long total = chatSessionJpaRepository.countTotalHandled();
        Double avgDuration = chatSessionJpaRepository.avgDuration();
        Double avgScore = chatSessionJpaRepository.avgScore();

        return DashboardKpiResponse.builder()
                .totalHandledCount(total)
                .avgDurationSec(avgDuration == null ? 0 : avgDuration)
                .avgScore(avgScore == null ? 0 : avgScore)
                .build();
    }

    // ======================================================
    // 2. 상태 비율 (Redis)
    // ======================================================
    public DashboardStatusRatioResponse getStatusRatio() {

        long waiting = redisRepository.countByStatus("WAITING");
        long inProgress = redisRepository.countByStatus("IN_PROGRESS");
        long ended = redisRepository.countByStatus("ENDED");
        long afterCall = redisRepository.countByStatus("AFTER_CALL");

        return DashboardStatusRatioResponse.builder()
                .waiting(waiting)
                .inProgress(inProgress)
                .ended(ended)
                .afterCall(afterCall)
                .build();
    }

    // ======================================================
    // 3. 오늘 상담 목록 조회
    // ======================================================
    public List<TodaySessionResponse> getTodaySessions() {

        List<Object[]> rows = chatSessionJpaRepository.findTodaySessionsRaw();

        return rows.stream()
                .map(r -> TodaySessionResponse.builder()
                        .sessionId(((Number) r[0]).longValue())
                        .userName((String) r[1])
                        .categoryName((String) r[2])
                        .startedAt((LocalDateTime) r[3])
                        .endedAt((LocalDateTime) r[4])
                        .status((String) r[5])
                        .build())
                .toList();
    }
}
