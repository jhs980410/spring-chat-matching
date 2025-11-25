package com.chatmatchingservice.springchatmatching.domain.chat.service.end;

import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class EndSessionTemplate {

    protected final ChatSessionRepository sessionRepository;
    protected final RedisTemplate<String, Object> redisTemplate;

    public final void endSession(Long sessionId, Long counselorId) {
        try {
            updateSessionStatus(sessionId);
            decreaseLoad(counselorId);
            markAfterCall(counselorId);
            saveLog(sessionId, counselorId);
            afterHook(sessionId, counselorId);

            log.info("[EndSession] 종료 처리 완료: sessionId={}, counselorId={}", sessionId, counselorId);
        } catch (Exception e) {
            log.error("[EndSession] 종료 템플릿 처리 중 예외: {}", e.getMessage(), e);
        }
    }

    protected void updateSessionStatus(Long sessionId) {
        sessionRepository.endSession(sessionId);
        redisTemplate.opsForValue().set(RedisKeyManager.sessionStatus(sessionId), "ENDED");
    }

    protected void decreaseLoad(Long counselorId) {
        redisTemplate.opsForValue()
                .increment(RedisKeyManager.counselorLoad(counselorId), -1);
    }

    protected void markAfterCall(Long counselorId) {
        redisTemplate.opsForValue()
                .set(RedisKeyManager.counselorStatus(counselorId), "AFTER_CALL");

        redisTemplate.opsForValue()
                .set(RedisKeyManager.counselorLastFinished(counselorId),
                        String.valueOf(System.currentTimeMillis()));
    }

    protected abstract void saveLog(Long sessionId, Long counselorId);

    protected void afterHook(Long sessionId, Long counselorId) {
        // optional
    }
}
