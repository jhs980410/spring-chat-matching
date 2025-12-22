package com.chatmatchingservice.springchatmatching.domain.ticket.service;

import com.chatmatchingservice.springchatmatching.domain.ticket.dto.*;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderStatus;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.MyOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyOrderService {

    private final MyOrderRepository myOrderRepository;

    /* =================================================
     * 예매 목록
     * ================================================= */
    public List<MyOrderResponseDto> getMyOrders(Long userId) {

        List<MyOrderRow> rows = myOrderRepository.findMyOrders(userId);

        Map<Long, MyOrderResponseDto> map = new LinkedHashMap<>();

        for (MyOrderRow row : rows) {
            map.computeIfAbsent(
                    row.getOrderId(),
                    id -> MyOrderResponseDto.from(row)
            ).getItems().add(
                    MyOrderItemResponseDto.from(row)
            );
        }

        return new ArrayList<>(map.values());
    }

    /* =================================================
     * 1️⃣ 주문 상세 (Row 기반, 다건 처리)
     * ================================================= */
    public MyOrderResponseDto getMyOrderDetail(Long userId, Long orderId) {

        List<MyOrderRow> rows =
                myOrderRepository.findOrderDetail(orderId, userId);

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("주문을 찾을 수 없습니다.");
        }

        MyOrderResponseDto dto = MyOrderResponseDto.from(rows.get(0));

        for (MyOrderRow row : rows) {
            dto.getItems().add(MyOrderItemResponseDto.from(row));
        }

        return dto;
    }

    /* =================================================
     * 2️⃣ 마이페이지 홈
     * ================================================= */
    public MyPageHomeResponseDto getMyPageHome(Long userId) {

        // 🔹 상태별 카운트
        long total = myOrderRepository.countByUser_Id(userId);
        long paid = myOrderRepository.countByUser_IdAndStatus(userId, TicketOrderStatus.PAID);
        long cancelled = myOrderRepository.countByUser_IdAndStatus(userId, TicketOrderStatus.CANCELLED);
        long refunded = myOrderRepository.countByUser_IdAndStatus(userId, TicketOrderStatus.REFUNDED);
        long completed = myOrderRepository.countCompleted(userId);

        // 🔹 최근 주문 (Row 재사용, 최대 3건)
        List<MyOrderRow> rows = myOrderRepository.findMyOrders(userId);

        Map<Long, MyPageHomeResponseDto.RecentOrder> recentMap = new LinkedHashMap<>();

        for (MyOrderRow row : rows) {
            recentMap.computeIfAbsent(
                    row.getOrderId(),
                    id -> new MyPageHomeResponseDto.RecentOrder(
                            row.getOrderId(),
                            row.getOrderStatus().name(),
                            row.getTotalPrice(),
                            row.getOrderedAt(),
                            EventSummaryDto.fromRow(
                                    row.getEventId(),
                                    row.getEventTitle(),
                                    row.getThumbnail(),
                                    row.getVenue(),
                                    row.getStartAt()
                            ),
                            0
                    )
            ).increaseQuantity(row.getQuantity());
        }

        List<MyPageHomeResponseDto.RecentOrder> recentOrders =
                recentMap.values().stream()
                        .limit(3)
                        .toList();

        // 🔹 유저 요약 (현재 더미)
        MyPageHomeResponseDto.UserSummary user =
                new MyPageHomeResponseDto.UserSummary(
                        userId,
                        "김철수",
                        "일반 회원",
                        0,
                        0
                );

        return new MyPageHomeResponseDto(
                user,
                new MyPageHomeResponseDto.OrderSummary(
                        total, paid, cancelled, refunded, completed
                ),
                recentOrders
        );
    }
}
