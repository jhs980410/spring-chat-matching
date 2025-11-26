package com.chatmatchingservice.springchatmatching.domain.counselor.service;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.service.matching.MatchingService;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselRequestDto;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingRequestService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatSessionRepository chatSessionRepository;
    private final MatchingService matchingService;


    // ============================================
    // 1. 대기열 등록 (enqueue)
    // ============================================
    @Transactional
    public Long enqueue(CounselRequestDto dto) {
        Long categoryId = dto.categoryId();
        Long userId = dto.userId();

        // 🔹 0. 파라미터 유효성 검사
        if (categoryId == null || userId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 🔹 1. 이미 WAITING 중인지 검사
        if (isUserAlreadyWaiting(userId)) {
            Long oldSessionId = findExistingWaitingSession(userId);
            if (oldSessionId != null) {
                log.info("[Waiting] 이미 WAITING 상태: userId={}, sessionId={}", userId, oldSessionId);
                throw new CustomException(ErrorCode.SESSION_ALREADY_EXISTS);
            }
        }

        // 🔹 2. DB 세션 생성
        ChatSession session = chatSessionRepository.createWaitingSession(userId, categoryId);
        Long sessionId = session.getId();

        try {
            // 🔹 3. Redis Queue push
            redisTemplate.opsForList().rightPush(
                    RedisKeyManager.categoryQueue(categoryId),
                    sessionId.toString()
            );

            // 🔹 4. Redis 세션 메타데이터 저장
            redisTemplate.opsForValue().set(RedisKeyManager.sessionStatus(sessionId), "WAITING");
            redisTemplate.opsForValue().set(RedisKeyManager.sessionUser(sessionId), userId);
            redisTemplate.opsForValue().set(RedisKeyManager.sessionCategory(sessionId), categoryId);

        } catch (Exception e) {
            log.error("[Waiting] Redis 저장 실패 → 롤백: sessionId={}", sessionId, e);

            // Redis queue rollback
            redisTemplate.opsForList().remove(
                    RedisKeyManager.categoryQueue(categoryId),
                    1,
                    sessionId.toString()
            );

            // 트랜잭션 롤백 → CustomException 변환
            throw new CustomException(ErrorCode.MATCHING_ERROR);
        }

        // 🔹 5. 매칭 시도
        try {
            matchingService.tryMatch(categoryId);
        } catch (Exception e) {
            log.error("[Waiting] 매칭 시도 중 오류: categoryId={}", categoryId, e);
            throw new CustomException(ErrorCode.MATCHING_ERROR);
        }

        return sessionId;
    }


    // ============================================
    // 2. 유저가 이미 WAITING인지 확인
    // ============================================
    private boolean isUserAlreadyWaiting(Long userId) {
        Set<String> keys = redisTemplate.keys("session:*:userId");
        if (keys == null || keys.isEmpty()) return false;

        for (String key : keys) {
            Object val = redisTemplate.opsForValue().get(key);
            if (val == null) continue;

            if (val.toString().equals(userId.toString())) {

                Long sid = extractSessionId(key);
                if (sid == null) continue;

                Object status = redisTemplate.opsForValue().get(RedisKeyManager.sessionStatus(sid));
                if ("WAITING".equals(status)) {
                    return true;
                }
            }
        }
        return false;
    }


    // ============================================
    // 3. WAITING 중인 기존 세션 ID 찾기
    // ============================================
    private Long findExistingWaitingSession(Long userId) {
        Set<String> keys = redisTemplate.keys("session:*:userId");
        if (keys == null || keys.isEmpty()) return null;

        for (String key : keys) {
            Object val = redisTemplate.opsForValue().get(key);
            if (val == null) continue;

            if (val.toString().equals(userId.toString())) {

                Long sid = extractSessionId(key);
                if (sid == null) continue;

                Object status = redisTemplate.opsForValue().get(RedisKeyManager.sessionStatus(sid));
                if ("WAITING".equals(status)) {
                    return sid;
                }
            }
        }
        return null;
    }


    // ============================================
    // 4. Redis key → sessionId 추출
    // ============================================
    private Long extractSessionId(String key) {
        try {
            return Long.valueOf(key.split(":")[1]);
        } catch (Exception e) {
            log.warn("[Waiting] sessionId 파싱 실패: key={}", key);
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
