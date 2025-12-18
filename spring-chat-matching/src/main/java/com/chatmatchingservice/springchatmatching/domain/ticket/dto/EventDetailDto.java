package com.chatmatchingservice.springchatmatching.domain.ticket.dto;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Event;
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

    private String category;     // MUSICAL / CONCERT ...
    private String thumbnail;    // 이미지 경로

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private String status;       // OPEN / SOLD_OUT / CLOSED

    private List<TicketOptionDto> ticketOptions;

    public static EventDetailDto from(Event event, List<TicketOptionDto> ticketOptions) {
        return EventDetailDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory().name())
                .thumbnail(event.getThumbnail())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .status(event.getStatus().name())
                .ticketOptions(ticketOptions)
                .build();
    }
}
