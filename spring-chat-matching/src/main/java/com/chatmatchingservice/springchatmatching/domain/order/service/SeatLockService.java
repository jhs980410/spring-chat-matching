package com.chatmatchingservice.springchatmatching.domain.order.service;

import com.chatmatchingservice.springchatmatching.domain.order.dto.SeatLockResultDto;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SeatLockService {
    //좌석 락 / 해제 (WRITE, Redis)
    private static final long LOCK_TTL_SECONDS = 500;

    private final RedisRepository redisRepository;

    public SeatLockResultDto lockSeats(
            Long userId,
            Long eventId,
            List<Long> seatIds
    ) {

        List<Long> locked = new ArrayList<>();
        List<Long> failed = new ArrayList<>();

        for (Long seatId : seatIds) {
            boolean success = redisRepository.tryLockSeat(
                    eventId, seatId, userId, LOCK_TTL_SECONDS
            );

            if (success) {
                locked.add(seatId);
                redisRepository.addUserLockedSeat(userId, eventId, seatId);
            } else {
                failed.add(seatId);
            }
        }

        if (!failed.isEmpty()) {
            locked.forEach(seatId ->
                    redisRepository.unlockSeat(eventId, seatId)
            );
            redisRepository.clearUserLockedSeats(userId, eventId);

            return new SeatLockResultDto(
                    false,
                    List.of(),
                    failed,
                    "이미 선택된 좌석이 있습니다."
            );
        }

        redisRepository.setReservationStatus(eventId, userId, "LOCKED");

        return new SeatLockResultDto(
                true,
                locked,
                List.of(),
                "좌석이 잠겼습니다."
        );
    }

    public void unlockSeats(Long userId, Long eventId) {
        Set<Long> seats =
                redisRepository.getUserLockedSeats(userId, eventId);

        for (Long seatId : seats) {
            redisRepository.unlockSeat(eventId, seatId);
        }

        redisRepository.clearUserLockedSeats(userId, eventId);
        redisRepository.clearReservationStatus(eventId, userId);
    }

    public Set<Long> getUserLockedSeats(Long userId, Long eventId) {
        return redisRepository.getUserLockedSeats(userId, eventId);
    }
}
