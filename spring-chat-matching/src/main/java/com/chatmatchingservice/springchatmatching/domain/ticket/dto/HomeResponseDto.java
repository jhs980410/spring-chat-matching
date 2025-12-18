package com.chatmatchingservice.springchatmatching.domain.ticket.dto;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.EventCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class HomeResponseDto {

    private List<HeroBannerDto> heroBanners;
    private List<EventSummaryDto> featuredEvents;
    private Map<EventCategory, List<EventSummaryDto>> rankings;
    private List<EventSummaryDto> openSoonEvents;
}
