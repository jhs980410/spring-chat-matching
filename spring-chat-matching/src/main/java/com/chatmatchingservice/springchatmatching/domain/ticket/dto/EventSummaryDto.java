package com.chatmatchingservice.springchatmatching.domain.ticket.dto;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EventSummaryDto {

    private Long id;
    private String title;
    private String category;   // MUSICAL / CONCERT
    private String thumbnail;

    private String badge;      // HOT / NEW / OPEN_SOON
    private Integer ranking;   // 랭킹용
    private String openDate;   // 오픈 예정용 (yyyy-MM-dd)

    /** 메인 노출 */
    public static EventSummaryDto from(Event event) {
        return EventSummaryDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .category(event.getCategory().name())
                .thumbnail(event.getThumbnail())
                .badge("HOT")
                .build();
    }

    /** 랭킹용 */
    public static EventSummaryDto fromWithRanking(Event event, int ranking) {
        return EventSummaryDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .category(event.getCategory().name())
                .thumbnail(event.getThumbnail())
                .ranking(ranking)
                .build();
    }

    /** 오픈 예정 */
    public static EventSummaryDto fromOpenSoon(Event event) {
        return EventSummaryDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .category(event.getCategory().name())
                .thumbnail(event.getThumbnail())
                .openDate(event.getStartAt().toLocalDate().toString())
                .badge("OPEN_SOON")
                .build();
    }
}
