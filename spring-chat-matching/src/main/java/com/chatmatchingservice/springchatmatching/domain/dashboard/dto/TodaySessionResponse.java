package com.chatmatchingservice.springchatmatching.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TodaySessionResponse {
    //오늘 상담 목록
    private Long sessionId;

    private String userName;
    private String categoryName;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String status;  // 문자열 (WAITING / IN_PROGRESS / ENDED 등)
}
