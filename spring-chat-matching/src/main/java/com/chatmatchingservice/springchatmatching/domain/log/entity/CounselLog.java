package com.chatmatchingservice.springchatmatching.domain.log.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "counsel_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true)
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "counselor_id", nullable = false)
    private Long counselorId;

    @Column(name = "domain_id", nullable = false)
    private Long domainId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Column(name = "after_call_sec")
    private Integer afterCallSec;

    @Column(name = "satisfaction_score")
    private Integer satisfactionScore;

    @Column(name = "feedback", length = 500)
    private String feedback;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}
