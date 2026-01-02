package com.chatmatchingservice.springchatmatching.domain.event.service;

import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingRoomService {

    private final RedisRepository redisRepository;
    private static final long ACCESS_TOKEN_TTL_MINUTES = 10;

    // 상태 정의용 Enum
    public enum WaitingStatus {
        AVAILABLE, WAITING, EXPIRED
    }

    // 응답 데이터 포맷 (Record 사용 시 간결함)
    public record WaitingStatusResponse(WaitingStatus status, Long rank, String message) {}

    /**
     * 1. 대기열 합류 및 상태 반환
     */
    public WaitingStatusResponse joinWaitingQueue(Long eventId, Long userId) {
        // 이미 입장권이 있다면 바로 입장 상태 반환
        if (redisRepository.hasAccessPass(eventId, userId)) {
            return new WaitingStatusResponse(WaitingStatus.AVAILABLE, 0L, "이미 입장 권한이 있습니다.");
        }

        long score = System.currentTimeMillis();
        redisRepository.addToWaitingQueue(eventId, userId, score);
        log.info("User {} joined waiting queue for event {}", userId, eventId);

        return checkStatus(eventId, userId);
    }

    /**
     * 2. 현재 상태 확인 (핵심 비즈니스 로직)
     */
    public WaitingStatusResponse checkStatus(Long eventId, Long userId) {
        // 1순위: 입장권(Access Pass)이 있는지 확인
        if (redisRepository.hasAccessPass(eventId, userId)) {
            return new WaitingStatusResponse(WaitingStatus.AVAILABLE, 0L, "입장이 가능합니다.");
        }

        // 2순위: 대기열 순번 조회
        Long rank = redisRepository.getWaitingRank(eventId, userId);

        // 3순위: 순번도 없고 입장권도 없으면 만료/미등록 상태
        if (rank == null) {
            return new WaitingStatusResponse(WaitingStatus.EXPIRED, null, "대기 세션이 만료되었거나 정보가 없습니다.");
        }

        // 4순위: 대기 중인 상태 (0번을 1번으로 변환하여 반환)
        return new WaitingStatusResponse(WaitingStatus.WAITING, rank + 1, (rank + 1) + "명 대기 중입니다.");
    }

    /**
     * 3. 진입 허용 처리 (Batch/Scheduler 전용)
     */
    public void allowEntry(Long eventId, int count) {
        Set<String> waitingUsers = redisRepository.popWaitingUsers(eventId, count);

        if (waitingUsers == null || waitingUsers.isEmpty()) return;

        for (String userIdStr : waitingUsers) {
            Long userId = Long.parseLong(userIdStr);
            redisRepository.setAccessPass(eventId, userId, ACCESS_TOKEN_TTL_MINUTES);
        }

        log.info("{} users allowed to enter event {}", waitingUsers.size(), eventId);
    }

    /**
     * 4. 입장 권한 확인 (인터셉터 등에서 활용)
     */
    public boolean canAccess(Long eventId, Long userId) {
        return redisRepository.hasAccessPass(eventId, userId);
    }
}