package com.chatmatchingservice.springchatmatching.domain.counselor.service;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.service.matching.MatchingService;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselRequestDto;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingRequestService {

    private final RedisRepository redisRepository;            // 🔥 RedisTemplate 제거
    private final ChatSessionRepository chatSessionRepository;
    private final MatchingService matchingService;


    // ============================================================
    // 1. 대기열 등록 (enqueue)
    // ============================================================
    @Transactional
    public Long enqueue(CounselRequestDto dto) {

        Long categoryId = dto.categoryId();
        Long userId = dto.userId();

        if (categoryId == null || userId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // --------------------------------------------------------
        // 1) WAITING 중복 검사
        // --------------------------------------------------------
        Long existingSessionId = findExistingWaitingSession(userId);
        if (existingSessionId != null) {
            log.info("[Waiting] 이미 WAITING: userId={}, sessionId={}", userId, existingSessionId);
            throw new CustomException(ErrorCode.SESSION_ALREADY_EXISTS);
        }

        // --------------------------------------------------------
        // 2) DB 세션 생성
        // --------------------------------------------------------
        ChatSession session = chatSessionRepository.createWaitingSession(userId, categoryId);
        Long sessionId = session.getId();

        try {
            // --------------------------------------------------------
            // 3) Redis queue push
            // --------------------------------------------------------
            redisRepository.enqueueSession(categoryId, sessionId);

            // --------------------------------------------------------
            // 4) Redis 메타데이터 저장
            // --------------------------------------------------------
            redisRepository.setSessionStatus(sessionId, "WAITING");
            redisRepository.setSessionUser(sessionId, userId);
            redisRepository.setSessionCategory(sessionId, categoryId);

        } catch (Exception e) {
            log.error("[Waiting] Redis enqueue 실패. sessionId={} → 롤백", sessionId, e);

            // Queue 롤백 처리
            try {
                // dequeue는 안전하게 제거 불가 → 직접 pop은 하지 않음
                // RedisRepository에 제거 기능 추가 시 적용 가능
            } catch (Exception ignored) { }

            throw new CustomException(ErrorCode.MATCHING_ERROR);
        }

        // --------------------------------------------------------
        // 5) 매칭 시도
        // --------------------------------------------------------
        try {
            matchingService.tryMatch(categoryId);
        } catch (Exception e) {
            log.error("[Waiting] 매칭 시도 중 예외: categoryId={}", categoryId, e);
            throw new CustomException(ErrorCode.MATCHING_ERROR);
        }

        return sessionId;
    }

    // ============================================================
    // 2. WAITING 중인 기존 세션 찾기
    // ============================================================
    private Long findExistingWaitingSession(Long userId) {

        try {
            // RedisRepository 기반 조회
            // session:*:userId 를 스캔하는 방식 제거 → repository 책임으로 넘김
            Long sessionId = redisRepository.findWaitingSessionByUser(userId);

            if (sessionId == null) return null;

            String status = redisRepository.getSessionStatus(sessionId);
            if ("WAITING".equals(status)) {
                return sessionId;
            }

            return null;

        } catch (Exception e) {
            log.error("[Waiting] findExistingWaitingSession 중 오류: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
