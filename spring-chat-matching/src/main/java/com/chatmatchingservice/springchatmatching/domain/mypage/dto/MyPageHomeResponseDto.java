package com.chatmatchingservice.springchatmatching.domain.mypage.dto;

import com.chatmatchingservice.springchatmatching.domain.event.dto.EventSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class MyPageHomeResponseDto {

    private UserSummary user;
    private OrderSummary orderSummary;
    private List<RecentOrder> recentOrders;

    @Getter
    @AllArgsConstructor
    public static class UserSummary {
        private Long userId;
        private String nickname;
        private String grade;
        private int couponCount;
        private int point;
    }

    @Getter
    @AllArgsConstructor
    public static class OrderSummary {
        private long total;
        private long paid;
        private long cancelled;
        private long refunded;
        private long completed;
    }

    @Getter
    @AllArgsConstructor
    public static class RecentOrder {
        private Long orderId;
        private String status;
        private int totalPrice;
        private LocalDateTime orderedAt;

        private EventSummaryDto event;
        private int quantity;
        public void increaseQuantity(int qty) {
            this.quantity += qty;
        }
    }
}
