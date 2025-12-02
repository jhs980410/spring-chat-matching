package com.chatmatchingservice.springchatmatching.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardKpiResponse {
   //KPI DTO
    private long totalHandledCount;   // 총 상담 건수
    private double avgDurationSec;    // 평균 상담 시간(초)
    private double avgScore;          // 평균 만족도
}
