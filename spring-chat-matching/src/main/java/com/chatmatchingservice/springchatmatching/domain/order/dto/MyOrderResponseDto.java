package com.chatmatchingservice.springchatmatching.domain.order.dto;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MyOrderResponseDto {

    private Long orderId;
    private TicketOrderStatus orderStatus;
    private LocalDateTime orderedAt;
    private Integer totalPrice;

    private EventInfo event;
    private List<MyOrderItemResponseDto> items = new ArrayList<>();

    private MyOrderResponseDto() {}

    public static MyOrderResponseDto from(MyOrderRow row) {
        MyOrderResponseDto res = new MyOrderResponseDto();
        res.orderId = row.getOrderId();
        res.orderStatus = row.getOrderStatus();
        res.orderedAt = row.getOrderedAt();
        res.totalPrice = row.getTotalPrice();
        res.event = EventInfo.from(row);
        return res;
    }

    @Getter
    public static class EventInfo {
        private Long eventId;
        private String title;
        private String thumbnail;
        private String venue;
        private LocalDateTime startAt;

        private static EventInfo from(MyOrderRow row) {
            EventInfo e = new EventInfo();
            e.eventId = row.getEventId();
            e.title = row.getEventTitle();
            e.thumbnail = row.getThumbnail();
            e.venue = row.getVenue();
            e.startAt = row.getStartAt();
            return e;
        }
    }
}
