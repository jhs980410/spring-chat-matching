package com.chatmatchingservice.springchatmatching.domain.chat.service.end;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class EndSessionTemplate {

    protected final ChatSessionRepository sessionRepository;
    protected final RedisRepository redisRepository;

    /**
     * Template Method
     */
    public final void endSession(Long sessionId, Long counselorId) {
        log.info("▶▶ endSession START: sessionId={}, counselorId={}", sessionId, counselorId);

        // 1) 검증
        ChatSession session = validateSession(sessionId, counselorId);

        try {
            // 2) 종료 상태 업데이트 (DB + Redis)
            updateSessionStatus(sessionId);

            // 3) 상담사 load 감소
            decreaseLoad(counselorId);

            // 4) 상담사 AFTER_CALL 처리
            markAfterCall(counselorId);

            // 5) Redis 세션 키 정리 + WS 이벤트 발행
            cleanupSessionKeys(sessionId, counselorId, session.getUserId());

            // 6) 로그 저장 (자식 클래스 구현)
            saveLog(sessionId, counselorId);

            // 7) 후처리 훅(옵션)
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

        if (sessionId == null || counselorId == null) {
            log.error("[Validate][FAIL] 기본 파라미터 null: sessionId={}, counselorId={}",
                    sessionId, counselorId);
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.error("[Validate][FAIL] sessionId={} → SESSION_NOT_FOUND", sessionId);
                    return new CustomException(ErrorCode.SESSION_NOT_FOUND);
                });

        // 상담사 ID null 체크 (DB 문제 대비)
        if (session.getCounselorId() == null) {
            log.error("[Validate][FAIL] DB에 counselorId=null → sessionId={}", sessionId);
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        // 상담사 불일치
        if (!counselorId.equals(session.getCounselorId())) {
            log.error("[Validate][FAIL] counselorId 불일치. 요청자={}, 실제={}",
                    counselorId, session.getCounselorId());
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        // 상태 null 방지
        if (session.getStatus() == null) {
            log.error("[Validate][FAIL] sessionId={} status=null (DB 비정상)", sessionId);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 이미 종료된 세션
        if (session.getStatus() == SessionStatus.ENDED ||
                session.getStatus() == SessionStatus.CANCELLED) {
            log.warn("[Validate][FAIL] 이미 종료된 세션: sessionId={}, status={}",
                    sessionId, session.getStatus());
            throw new CustomException(ErrorCode.SESSION_ALREADY_FINISHED);
        }

        log.info("[Validate][SUCCESS] sessionId={} counselorId={} status={}",
                sessionId, counselorId, session.getStatus());

        return session;
    }


    /**
     * 2) DB + Redis 상태 업데이트
     */
    protected void updateSessionStatus(Long sessionId) {
        try {
            sessionRepository.endSession(sessionId);              // DB: 상태 ENDED
            redisRepository.setSessionStatus(sessionId, "ENDED"); // Redis
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 3) 상담사 load 감소
     */
    /**
     * 3) 상담사 load 감소 (안정화 버전)
     */
    protected void decreaseLoad(Long counselorId) {
        try {
            // load -1 감소 수행
            Long newLoad = redisRepository.incrementCounselorLoad(counselorId, -1);

            // Redis 키가 없었거나 null이면 0으로 초기화
            if (newLoad == null) {
                log.warn("[EndSession] counselorId={} load 키 없음 → 0으로 초기화", counselorId);
                redisRepository.setCounselorLoad(counselorId, 0L);
                return;
            }

            // 음수로 내려갔으면 안전하게 0으로 보정
            if (newLoad < 0) {
                log.warn("[EndSession] counselorId={} load 음수({}) → 0으로 보정", counselorId, newLoad);
                redisRepository.setCounselorLoad(counselorId, 0L);
            }

        } catch (CustomException e) {
            // 이미 정의된 CustomException이면 그대로 던짐
            log.error("[EndSession] decreaseLoad CustomException: {}", e.getErrorCode().getCode());
            throw e;

        } catch (Exception e) {
            // Redis 문제 등 예상못한 예외만 서버오류 처리
            log.error("[EndSession] decreaseLoad 처리 중 예외: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * 4) 상담사 AFTER_CALL 상태 적용
     */
    protected void markAfterCall(Long counselorId) {
        try {
            redisRepository.setCounselorStatus(counselorId, "AFTER_CALL");
            redisRepository.setCounselorLastFinished(
                    counselorId,
                    System.currentTimeMillis()
            );
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 5) Redis 세션 데이터 완전 정리 + WebSocket 종료 이벤트 발행
     */
    protected void cleanupSessionKeys(Long sessionId, Long counselorId, Long userId) {
        try {
            Long categoryId = redisRepository.getSessionCategory(sessionId);

            // 5-1. WebSocket 종료 이벤트 발행
            SessionEndEvent event = SessionEndEvent.of(sessionId, counselorId, userId);
            redisRepository.publishToWsChannel(sessionId, event);

            // 5-2. 대기열에서 해당 세션 제거 (매칭 중 취소/종료 대비)
            if (categoryId != null) {
                redisRepository.removeFromQueue(categoryId, sessionId);
            }

            // 5-3. 세션 관련 Redis 키 전체 삭제
            redisRepository.deleteSessionKeys(sessionId);

        } catch (Exception e) {
            log.error("[EndSession] Redis 정리 중 오류: sessionId={}, counselorId={}",
                    sessionId, counselorId, e);
        }
    }


    /**
     * 6) 로그 저장 (자식 클래스가 직접 구현)
     */
    protected abstract void saveLog(Long sessionId, Long counselorId);


    /**
     * 7) 후처리 훅 (선택)
     */
    protected void afterHook(Long sessionId, Long counselorId) {
        // optional
    }
}
