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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CounselorStatusService {

    private final RedisRepository redisRepository;
    private final MatchingService matchingService;

    private static final long ZERO_LOAD = 0L;

    // ============================
    // READY (ONLINE)
    // ============================
    @Transactional
    public void ready(Long counselorId, List<Long> categoryIds) {

        if (categoryIds == null || categoryIds.isEmpty())
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);

        // 상태 설정
        redisRepository.setCounselorStatus(counselorId, CounselorStatus.ONLINE.name());

        // load 초기화
        redisRepository.setCounselorLoad(counselorId, ZERO_LOAD);

        // 상담사 카테고리 Set 등록
        redisRepository.setCounselorCategories(counselorId, categoryIds);

        // 카테고리 후보군에 상담사 추가
        for (Long categoryId : categoryIds) {
            redisRepository.addCounselorToCategory(categoryId, counselorId);
        }

        // 매칭 트리거
        for (Long categoryId : categoryIds) {
            matchingService.tryMatch(categoryId);
        }

        log.info("[READY] counselorId={}, categories={}", counselorId, categoryIds);
    }


    // ============================
    // BUSY
    // ============================
    @Transactional
    public void updateStatus(Long counselorId, CounselorStatusUpdateRequest req) {

        if (req.getStatus() == null)
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);

        redisRepository.setCounselorStatus(counselorId, req.getStatus().name());

        log.info("[BUSY] counselorId={}", counselorId);
    }


    // ============================
    // AFTER CALL
    // ============================
    @Transactional
    public void setAfterCall(Long counselorId) {

        redisRepository.setCounselorStatus(counselorId, CounselorStatus.AFTER_CALL.name());
        redisRepository.incrementCounselorLoad(counselorId, -1L);
        redisRepository.setCounselorLastFinished(counselorId, System.currentTimeMillis());

        log.info("[AFTER_CALL] counselorId={}", counselorId);
    }


    // ============================
    // OFFLINE
    // ============================
    @Transactional
    public void offline(Long counselorId) {

        // 상태 OFFLINE
        redisRepository.setCounselorStatus(counselorId, CounselorStatus.OFFLINE.name());

        // load 0 초기화
        redisRepository.setCounselorLoad(counselorId, ZERO_LOAD);

        // 모든 카테고리에서 상담사 제거
        List<Long> categories = redisRepository.getCounselorCategories(counselorId);

        for (Long categoryId : categories) {
            redisRepository.removeCounselorFromCategory(categoryId, counselorId);
        }

        // redis에서 상담사의 카테고리 정보 삭제
        redisRepository.deleteCounselorCategories(counselorId);

        log.info("[OFFLINE] counselorId={}, removedCategories={}", counselorId, categories);
    }
}
