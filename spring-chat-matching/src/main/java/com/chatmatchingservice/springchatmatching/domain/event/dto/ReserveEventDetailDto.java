package com.chatmatchingservice.springchatmatching.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReserveEventDetailDto {
    //예매 전용 DTO
    private Long eventId;

    private String title;
    private String venue;

    private List<SectionResponseDto> sections;
}
