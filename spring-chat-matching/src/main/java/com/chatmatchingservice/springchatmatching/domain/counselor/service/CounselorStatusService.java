package com.chatmatchingservice.springchatmatching.domain.counselor.service;

import com.chatmatchingservice.springchatmatching.domain.chat.service.matching.MatchingService;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorStatusUpdateRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorStatus;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CounselorStatusService {

    private final RedisRepository redisRepository;
    private final MatchingService matchingService;

    private static final long ZERO_LOAD = 0L;

    /**
     * 상담사 상태 업데이트 (ONLINE / OFFLINE / AFTER_CALL / BUSY)
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
            redisRepository.setCounselorStatus(counselorId, status.name());

            // -------------------------------------------------------
            // OFFLINE 처리
            // -------------------------------------------------------
            if (status == CounselorStatus.OFFLINE) {

                redisRepository.setCounselorLoad(counselorId, ZERO_LOAD);

                if (categoryId != null) {
                    redisRepository.removeCounselorFromCategory(categoryId, counselorId);
                }

                log.info("[CounselorStatus] OFFLINE → counselorId={}", counselorId);
                return;
            }

            // -------------------------------------------------------
            // 온라인 / 대기 상태 처리
            // -------------------------------------------------------

            // 후보군 유지
            if (categoryId != null) {
                redisRepository.addCounselorToCategory(categoryId, counselorId);
            }

            // ONLINE → Load 0 초기화
            if (status == CounselorStatus.ONLINE) {
                redisRepository.setCounselorLoad(counselorId, ZERO_LOAD);
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
            // 상태 변경
            redisRepository.setCounselorStatus(counselorId, "AFTER_CALL");

            // Load -1
            redisRepository.incrementCounselorLoad(counselorId, -1L);

            // 마지막 상담 종료시간 기록
            redisRepository.setCounselorLastFinished(counselorId, System.currentTimeMillis());

            log.info("[CounselorStatus] AFTER_CALL 처리 완료: counselorId={}", counselorId);

        } catch (Exception e) {
            log.error("[CounselorStatus] setAfterCall 중 예외: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
