package com.chatmatchingservice.springchatmatching.domain.stats.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CounselorHandledResponse {
    //상담사별 총 처리량
    private Long counselorId;
    private String counselorName;
    private long handledCount;
}
