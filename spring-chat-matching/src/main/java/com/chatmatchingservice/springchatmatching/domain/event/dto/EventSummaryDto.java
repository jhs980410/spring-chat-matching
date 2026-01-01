package com.chatmatchingservice.springchatmatching.domain.event.dto;

import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSummaryDto {

    private Long id;
    private String title;

    private String categoryCode;   // MUSICAL
    private String categoryName;   // 뮤지컬

    private String thumbnail;

    private String badge;      // HOT / NEW / OPEN_SOON
    private Integer ranking;   // 랭킹용
    private String openDate;   // yyyy-MM-dd
    private LocalDateTime startAt;
    /** 메인 노출 */
    public static EventSummaryDto from(Event event) {
        return EventSummaryDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .categoryCode(event.getCategory().getCode())
                .categoryName(event.getCategory().getName())
                .thumbnail(event.getThumbnail())
                .badge("HOT")
                .build();
    }

    /** 랭킹용 */
    public static EventSummaryDto fromWithRanking(Event event, int ranking) {
        return EventSummaryDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .categoryCode(event.getCategory().getCode())
                .categoryName(event.getCategory().getName())
                .thumbnail(event.getThumbnail())
                .ranking(ranking)
                .build();
    }

    /** 오픈 예정 */
    public static EventSummaryDto fromOpenSoon(Event event) {
        return EventSummaryDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .categoryCode(event.getCategory().getCode())
                .categoryName(event.getCategory().getName())
                .thumbnail(event.getThumbnail())
                .openDate(event.getStartAt().toLocalDate().toString())
                .badge("OPEN_SOON")
                .build();
    }
    public static EventSummaryDto fromRow(
            Long eventId,
            String title,
            String thumbnail,
            String venue,
            LocalDateTime startAt
    ) {
        return EventSummaryDto.builder()
                .id(eventId)
                .title(title)
                .thumbnail(thumbnail)
                .startAt(startAt)
                .build();
    }

}
