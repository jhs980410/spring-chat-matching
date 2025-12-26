package com.chatmatchingservice.springchatmatching.domain.payment.service;

import com.chatmatchingservice.springchatmatching.domain.order.dto.SeatLockResultDto;
import com.chatmatchingservice.springchatmatching.domain.order.service.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final SeatLockService seatLockService;

    public void prepareReservation(
            Long userId,
            Long eventId,
            List<Long> seatIds
    ) {
        // 중복 예매 방지
        String status =
                seatLockService.getUserLockedSeats(userId, eventId).isEmpty()
                        ? null
                        : "IN_PROGRESS";

        if ("IN_PROGRESS".equals(status)) {
            throw new IllegalStateException("이미 예매 진행 중입니다.");
        }

        SeatLockResultDto result =
                seatLockService.lockSeats(userId, eventId, seatIds);

        if (!result.success()) {
            throw new IllegalStateException(result.message());
        }
    }
}
