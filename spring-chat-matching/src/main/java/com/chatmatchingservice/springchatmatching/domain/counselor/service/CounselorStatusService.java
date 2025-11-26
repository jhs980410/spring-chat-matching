package com.chatmatchingservice.springchatmatching.domain.counselor.service;

import com.chatmatchingservice.springchatmatching.domain.chat.service.matching.MatchingService;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorStatusUpdateRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorStatus;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
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

    /**
     * 상담사 상태 업데이트 (ONLINE/OFFLINE/AFTER_CALL/BUSY)
     */
    @Transactional
    public void updateStatus(long counselorId, CounselorStatusUpdateRequest req) {

        try {
            CounselorStatus status = req.getStatus();
            Long categoryId = req.getCategoryId();

            if (status == null) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            // 1) 상태 저장
            redisTemplate.opsForValue().set(
                    RedisKeyManager.counselorStatus(counselorId), status.name()
            );

            // ============================
            // OFFLINE 처리
            // ============================
            if (status == CounselorStatus.OFFLINE) {

                // Load 초기화
                redisTemplate.opsForValue().set(
                        RedisKeyManager.counselorLoad(counselorId), ZERO_LOAD
                );

                // 카테고리 후보군 제거
                if (categoryId != null) {
                    redisTemplate.opsForSet().remove(
                            RedisKeyManager.categoryCounselors(categoryId), counselorId
                    );
                }

                log.info("[CounselorStatus] OFFLINE → counselorId={}", counselorId);
                return;
            }

            // ============================
            // 온라인/대기 상태 처리
            // ============================

            // OFFLINE이 아니면 카테고리 후보군에 유지
            if (categoryId != null) {
                redisTemplate.opsForSet().add(
                        RedisKeyManager.categoryCounselors(categoryId), counselorId
                );
            }

            // ONLINE → Load = 0으로 리셋
            if (status == CounselorStatus.ONLINE) {
                redisTemplate.opsForValue().set(
                        RedisKeyManager.counselorLoad(counselorId), ZERO_LOAD
                );
            }

            // READY 상태 → 매칭 트리거
            if (status == CounselorStatus.ONLINE || status == CounselorStatus.AFTER_CALL) {
                if (categoryId != null) {
                    matchingService.tryMatch(categoryId);
                }
            }

            log.info("[CounselorStatus] UPDATE: counselorId={}, status={}, categoryId={}",
                    counselorId, status, categoryId);

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            log.error("[CounselorStatus] updateStatus 중 예외: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 종료 후 AFTER_CALL 상태 처리
     */
    @Transactional
    public void setAfterCall(Long counselorId) {

        try {
            redisTemplate.opsForValue().set(
                    RedisKeyManager.counselorStatus(counselorId),
                    "AFTER_CALL"
            );

            redisTemplate.opsForValue().increment(
                    RedisKeyManager.counselorLoad(counselorId),
                    -1
            );

            redisTemplate.opsForValue().set(
                    RedisKeyManager.counselorLastFinished(counselorId),
                    String.valueOf(System.currentTimeMillis())
            );

            log.info("[CounselorStatus] AFTER_CALL: counselorId={}", counselorId);

        } catch (Exception e) {
            log.error("[CounselorStatus] setAfterCall 중 예외: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
