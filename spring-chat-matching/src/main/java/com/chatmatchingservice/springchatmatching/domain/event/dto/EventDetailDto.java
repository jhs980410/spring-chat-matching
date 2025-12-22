package com.chatmatchingservice.springchatmatching.domain.event.dto;

import com.chatmatchingservice.springchatmatching.domain.ticket.dto.TicketOptionDto;
import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class EventDetailDto {

    private Long id;
    private String title;
    private String description;

    private String categoryCode; // MUSICAL
    private String categoryName; // 뮤지컬

    private String thumbnail;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private String status;

    private List<TicketOptionDto> ticketOptions;

    public static EventDetailDto from(Event event, List<TicketOptionDto> ticketOptions) {
        return EventDetailDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .categoryCode(event.getCategory().getCode())
                .categoryName(event.getCategory().getName())
                .thumbnail(event.getThumbnail())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .status(event.getStatus().name())
                .ticketOptions(ticketOptions)
                .build();
    }
}
