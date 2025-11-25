package com.chatmatchingservice.springchatmatching.domain.counselor.dto;

import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CounselorStatusUpdateRequest {
    private Long counselorId;
    private CounselorStatus status;
    private Long categoryId;
}