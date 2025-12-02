package com.chatmatchingservice.springchatmatching.domain.stats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "counselor_stats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_stats_date", columnNames = {"counselor_id", "stat_date"})
        }
)
public class CounselorStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "counselor_id", nullable = false)
    private Long counselorId;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "handled_count")
    private Integer handledCount;

    @Column(name = "avg_duration_sec")
    private Integer avgDurationSec;

    @Column(name = "avg_score")
    private Double avgScore;

    @Column(name = "response_rate")
    private Double responseRate;

    @Column(name = "success_rate")
    private Double successRate;
}
