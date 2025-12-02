package com.chatmatchingservice.springchatmatching.domain.stats.service;

import com.chatmatchingservice.springchatmatching.domain.stats.dto.CounselorHandledResponse;
import com.chatmatchingservice.springchatmatching.domain.stats.dto.DailyStatsResponse;
import com.chatmatchingservice.springchatmatching.domain.stats.entity.CounselorStats;
import com.chatmatchingservice.springchatmatching.domain.stats.repository.CounselorStatsRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final CounselorStatsRepository counselorStatsRepository;
    private final ChatSessionJpaRepository chatSessionJpaRepository;

    // ======================================================
    // 1. 일자별 통계
    // ======================================================
    public List<DailyStatsResponse> getDailyStats() {

        List<CounselorStats> stats = counselorStatsRepository.findAllStats();

        return stats.stream()
                .map(s -> DailyStatsResponse.builder()
                        .statDate(s.getStatDate())
                        .handledCount(s.getHandledCount())
                        .avgDurationSec(s.getAvgDurationSec())
                        .avgScore(s.getAvgScore())
                        .build())
                .toList();
    }

    // ======================================================
    // 2. 상담사별 총 처리량 (막대 그래프)
    // ======================================================
    public List<CounselorHandledResponse> getCounselorHandled() {

        List<Object[]> rows = chatSessionJpaRepository.getCounselorHandled();

        return rows.stream()
                .map(r -> CounselorHandledResponse.builder()
                        .counselorId(((Number) r[0]).longValue())
                        .counselorName((String) r[1])
                        .handledCount(((Number) r[2]).longValue())
                        .build()
                ).toList();
    }
}
