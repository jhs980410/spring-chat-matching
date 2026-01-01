package com.chatmatchingservice.springchatmatching.domain.mypage.dto;

import com.chatmatchingservice.springchatmatching.domain.event.dto.EventSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeResponseDto {

    private List<HeroBannerDto> heroBanners;
    private List<EventSummaryDto> featuredEvents;
    private Map<String , List<EventSummaryDto>> rankings;
    private List<EventSummaryDto> openSoonEvents;
}
