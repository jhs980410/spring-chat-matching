package com.chatmatchingservice.springchatmatching.domain.stats.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailyStatsResponse {
    //일별 통계 DTO
    private LocalDate statDate;
    private int handledCount;
    private double avgDurationSec;
    private double avgScore;  // 평균 만족도도 함께 제공
}
