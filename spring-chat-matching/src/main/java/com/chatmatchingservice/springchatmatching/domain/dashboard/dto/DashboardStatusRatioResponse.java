package com.chatmatchingservice.springchatmatching.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardStatusRatioResponse {
    //상담 상태 비율
    private long waiting;
    private long inProgress;
    private long ended;
    private long afterCall;
}
