package com.chatmatchingservice.springchatmatching.domain.counselor.service;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.service.matching.MatchingService;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselRequestDto;
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

    /**
     * 고객 상담 요청 enqueue
     */
    @Transactional
    public Long enqueue(CounselRequestDto dto) {
        Long categoryId = dto.categoryId();
        Long userId = dto.userId();

        // 0) 기본 유효성 검사
        if (categoryId == null || userId == null) {
            throw new IllegalArgumentException("categoryId 또는 userId가 null입니다.");
        }

        // 1) 이미 WAITING 중인지 검사 (중복 enqueue 방지)
        if (isUserAlreadyWaiting(userId)) {
            Long oldSessionId = findExistingWaitingSession(userId);
            log.info("[Waiting] 이미 WAITING 상태: userId={}, sessionId={}", userId, oldSessionId);
            return oldSessionId; // 기존 세션 재사용
        }

        // 2) DB WAITING 세션 생성
        ChatSession session = chatSessionRepository.createWaitingSession(userId, categoryId);
        Long sessionId = session.getId();

        try {
            // 3) Redis Queue Push
            redisTemplate.opsForList().rightPush(
                    RedisKeyManager.categoryQueue(categoryId),
                    sessionId.toString()
            );

            // 4) Redis 메타데이터 적재
            redisTemplate.opsForValue().set(RedisKeyManager.sessionStatus(sessionId), "WAITING");
            redisTemplate.opsForValue().set(RedisKeyManager.sessionUser(sessionId), userId);
            redisTemplate.opsForValue().set(RedisKeyManager.sessionCategory(sessionId), categoryId);

        } catch (Exception e) {
            // Redis 실패 → DB 롤백 (Transactional), Redis도 정리
            log.error("[Waiting] Redis 저장 실패, sessionId={} 롤백", sessionId, e);
            redisTemplate.opsForList().remove(
                    RedisKeyManager.categoryQueue(categoryId), 1, sessionId.toString()
            );
            throw e;
        }

        // 5) 매칭 시도
        matchingService.tryMatch(categoryId);

        return sessionId;
    }

    /**
     * 현재 유저가 WAITING 상태인지 검사
     */
    private boolean isUserAlreadyWaiting(Long userId) {
        Set<String> keys = redisTemplate.keys("session:*:userId");
        if (keys == null) return false;

        for (String key : keys) {
            Object val = redisTemplate.opsForValue().get(key);
            if (val != null && val.toString().equals(userId.toString())) {
                // 세션ID 추출
                Long sid = extractSessionId(key);
                String status = (String) redisTemplate.opsForValue()
                        .get(RedisKeyManager.sessionStatus(sid));
                if ("WAITING".equals(status)) return true;
            }
        }
        return false;
    }

    /**
     * 이미 WAITING 상태 세션 ID 검색
     */
    private Long findExistingWaitingSession(Long userId) {
        Set<String> keys = redisTemplate.keys("session:*:userId");
        for (String key : keys) {
            Object val = redisTemplate.opsForValue().get(key);
            if (val == null) continue;
            if (val.toString().equals(userId.toString())) {
                Long sid = extractSessionId(key);
                String status = (String) redisTemplate.opsForValue()
                        .get(RedisKeyManager.sessionStatus(sid));
                if ("WAITING".equals(status)) return sid;
            }
        }
        return null;
    }

    /**
     * Redis 키에서 sessionId 추출: session:{id}:userId
     */
    private Long extractSessionId(String key) {
        try {
            return Long.valueOf(key.split(":")[1]);
        } catch (Exception e) {
            return null;
        }
    }
}
