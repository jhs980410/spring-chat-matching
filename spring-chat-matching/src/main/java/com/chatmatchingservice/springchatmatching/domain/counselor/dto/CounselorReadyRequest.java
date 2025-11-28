package com.chatmatchingservice.springchatmatching.domain.counselor.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CounselorReadyRequest {
    private List<Long> categoryIds;   // 상담사가 READY할 카테고리 목록
}
