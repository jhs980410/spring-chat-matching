package com.chatmatchingservice.springchatmatching.domain.ticket.service;

import com.chatmatchingservice.springchatmatching.domain.ticket.dto.MyOrderItemResponseDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.dto.MyOrderResponseDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.dto.MyOrderRow;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.MyOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyOrderService {

    private final MyOrderRepository myOrderRepository;

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
}
