package com.chatmatchingservice.springchatmatching.domain.dashboard.service;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionJpaRepository;
import com.chatmatchingservice.springchatmatching.domain.dashboard.dto.*;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
        System.out.println("준비인원:" + waiting);
        long inProgress = redisRepository.countByStatus("IN_PROGRESS");
        System.out.println("상담인원" + inProgress);
        long ended = redisRepository.countByStatus("ENDED");
        System.out.println("종료" + ended);
        long afterCall = redisRepository.countByStatus("AFTER_CALL");
        System.out.println("에프터" + afterCall);
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
                .map(r -> {
                    // java.sql.Timestamp로 받아서 toLocalDateTime() 호출
                   Timestamp startedTimestamp = (java.sql.Timestamp) r[3];
                   Timestamp endedTimestamp = (java.sql.Timestamp) r[4];

                    // Null 체크 추가: DB 컬럼이 NULLABLE일 경우 안전하게 처리
                    LocalDateTime startedAt = (startedTimestamp != null) ? startedTimestamp.toLocalDateTime() : null;
                    LocalDateTime endedAt = (endedTimestamp != null) ? endedTimestamp.toLocalDateTime() : null;

                    return TodaySessionResponse.builder()
                            .sessionId(((Number) r[0]).longValue())
                            .userName((String) r[1])
                            .categoryName((String) r[2])
                            .startedAt(startedAt) // 변환된 LocalDateTime 사용
                            .endedAt(endedAt)     // 변환된 LocalDateTime 사용
                            .status((String) r[5])
                            .build();
                })
                .toList();
    }
}