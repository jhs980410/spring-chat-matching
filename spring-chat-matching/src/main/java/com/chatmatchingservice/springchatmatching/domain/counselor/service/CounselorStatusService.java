package com.chatmatchingservice.springchatmatching.domain.counselor.service;

import com.chatmatchingservice.springchatmatching.domain.chat.service.matching.MatchingService;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorStatusUpdateRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorStatus;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class CounselorStatusService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MatchingService matchingService;

    // Load 초기화 상수
    private static final String ZERO_LOAD = "0";

    @Transactional
    public void updateStatus(long counselorId, CounselorStatusUpdateRequest req) {

        CounselorStatus status = req.getStatus();
        Long categoryId = req.getCategoryId();

        // 1. 상태 저장 (모든 상태에 공통)
        redisTemplate.opsForValue().set(
                RedisKeyManager.counselorStatus(counselorId), status.name()
        );

        // 2. 상태별 Redis 및 매칭 처리
        if (status == CounselorStatus.OFFLINE) {
            // 2-1. Load 초기화: OFFLINE 시 Load를 0으로 리셋 (부하 정리)
            redisTemplate.opsForValue().set(
                    RedisKeyManager.counselorLoad(counselorId), ZERO_LOAD
            );

            // 2-2. 카테고리 Set에서 제거 (스킬 기반 매칭 후보군에서 제외)
            if (categoryId != null)
                redisTemplate.opsForSet().remove(
                        RedisKeyManager.categoryCounselors(categoryId), counselorId
                );
            return;
        }

        // --- ONLINE / AFTER_CALL / BUSY 처리 ---

        // 3. 카테고리 Set 등록 (ONLINE, AFTER_CALL, BUSY 등 매칭 가능한 카테고리에 등록)
        // OFFLINE만 아니면 카테고리 Set에 등록하여 스킬 기반 후보군에 유지
        if (categoryId != null)
            redisTemplate.opsForSet().add(
                    RedisKeyManager.categoryCounselors(categoryId), counselorId
            );

        // 4. ONLINE 상태 특화 처리
        if (status == CounselorStatus.ONLINE) {
            // 4-1. Load 초기화: ONLINE 시 Load를 0으로 강제 리셋 (새로운 READY 상태 보장)
            redisTemplate.opsForValue().set(
                    RedisKeyManager.counselorLoad(counselorId), ZERO_LOAD
            );
        }

        // 5. READY 상태 시 매칭 트리거 (ONLINE과 AFTER_CALL)
        if (status == CounselorStatus.ONLINE || status == CounselorStatus.AFTER_CALL) {
            // READY 상태(ONLINE/AFTER_CALL) 진입 시 즉시 매칭 시도
            if (categoryId != null)
                matchingService.tryMatch(categoryId);
        }
    }

    @Transactional
    public void setAfterCall(Long counselorId) {

        // Redis: 상담사 상태를 AFTER_CALL로 설정
        redisTemplate.opsForValue().set(
                RedisKeyManager.counselorStatus(counselorId),
                "AFTER_CALL"
        );

        // Load 감소(선택)
        redisTemplate.opsForValue().increment(
                RedisKeyManager.counselorLoad(counselorId),
                -1
        );

        // 최근 종료 시간 기록
        redisTemplate.opsForValue().set(
                RedisKeyManager.counselorLastFinished(counselorId),
                String.valueOf(System.currentTimeMillis())
        );

        log.info("[Service] Counselor AFTER_CALL: counselorId={}", counselorId);
    }

}
