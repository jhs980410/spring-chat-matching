package com.chatmatchingservice.springchatmatching.domain.counselor.dto;

import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CounselorStatusUpdateRequest {
    private Long counselorId;
    private CounselorStatus status;
    private Long categoryId;
}