package com.chatmatchingservice.springchatmatching.domain.stats.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "daily_stats",
        uniqueConstraints = {
                // counselor_id가 null이면 전체 집계, 아니면 상담사별 집계
                @UniqueConstraint(name = "uk_daily_stats_date_counselor", columnNames = {"stat_date", "counselor_id"})
        },
        indexes = {
                @Index(name = "idx_daily_stats_date", columnList = "stat_date")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 통계 기준 일자
    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    // 전체/상담사별 구분용 (null 이면 전체)
    @Column(name = "counselor_id")
    private Long counselorId;

    // 해당 일자에 처리된 상담 수
    @Column(name = "handled_count", nullable = false)
    private int handledCount;

    // 평균 상담 시간(초)
    @Column(name = "avg_duration_sec", nullable = false)
    private double avgDurationSec;

    // 평균 만족도
    @Column(name = "avg_score", nullable = false)
    private double avgScore;
}
