package com.chatmatchingservice.springchatmatching.domain.chat.service.end;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class EndSessionTemplate {

    protected final ChatSessionRepository sessionRepository;
    protected final RedisTemplate<String, Object> redisTemplate;

    /**
     * Template Method
     */
    public final void endSession(Long sessionId, Long counselorId) {

        // 1) 검증
        ChatSession session = validateSession(sessionId, counselorId);

        try {
            // 2) 종료 상태 업데이트
            updateSessionStatus(sessionId);

            // 3) 상담사 load 감소
            decreaseLoad(counselorId);

            // 4) 상담사 AFTER_CALL 처리
            markAfterCall(counselorId);

            // 5) 로그 저장 (자식 클래스 구현)
            saveLog(sessionId, counselorId);

            // 6) 후처리 훅(옵션)
            afterHook(sessionId, counselorId);

            log.info("[EndSession] 종료 처리 완료: sessionId={}, counselorId={}",
                    sessionId, counselorId);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("[EndSession] 템플릿 처리 중 예외: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 1) 세션 유효성 검증
     */
    private ChatSession validateSession(Long sessionId, Long counselorId) {

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        // 상담사 불일치
        if (!counselorId.equals(session.getCounselorId())) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        // 이미 종료된 세션
        if (session.getStatus() == SessionStatus.ENDED ||
                session.getStatus() == SessionStatus.CANCELLED) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_FINISHED);
        }

        return session;
    }


    /**
     * 2) DB + Redis 상태 업데이트
     */
    protected void updateSessionStatus(Long sessionId) {
        try {
            sessionRepository.endSession(sessionId);  // DB: 상태 ENDED
            redisTemplate.opsForValue()
                    .set(RedisKeyManager.sessionStatus(sessionId), "ENDED");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 3) 상담사 load 감소
     */
    protected void decreaseLoad(Long counselorId) {
        try {
            redisTemplate.opsForValue()
                    .increment(RedisKeyManager.counselorLoad(counselorId), -1);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 4) 상담사 AFTER_CALL 상태 적용
     */
    protected void markAfterCall(Long counselorId) {
        try {
            redisTemplate.opsForValue()
                    .set(RedisKeyManager.counselorStatus(counselorId), "AFTER_CALL");

            redisTemplate.opsForValue()
                    .set(RedisKeyManager.counselorLastFinished(counselorId),
                            String.valueOf(System.currentTimeMillis()));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 5) 로그 저장 (자식 클래스가 직접 구현)
     */
    protected abstract void saveLog(Long sessionId, Long counselorId);


    /**
     * 6) 후처리 훅 (선택)
     */
    protected void afterHook(Long sessionId, Long counselorId) {
        // optional
    }
}
