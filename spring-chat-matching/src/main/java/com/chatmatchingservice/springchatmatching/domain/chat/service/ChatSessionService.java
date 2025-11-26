package com.chatmatchingservice.springchatmatching.domain.chat.service;

import com.chatmatchingservice.springchatmatching.domain.chat.dto.ChatMessageResponse;
import com.chatmatchingservice.springchatmatching.domain.chat.dto.SessionInfoResponse;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatMessage;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatMessageRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.service.end.EndSessionFacade;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final EndSessionFacade endSessionFacade;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatSessionEventService eventService;

    // =========================================================
    // 1. 현재 진행 중인 세션 조회
    // =========================================================
    public SessionInfoResponse getSessionOfUserOrCounselor(Long id) {

        Optional<ChatSession> userSession =
                chatSessionRepository.findActiveSessionByUser(id);

        if (userSession.isPresent()) {
            return toResponse(userSession.get());
        }

        Optional<ChatSession> counselorSession =
                chatSessionRepository.findActiveSessionByCounselor(id);

        if (counselorSession.isPresent()) {
            return toResponse(counselorSession.get());
        }

        return new SessionInfoResponse(null, "NONE", null, null, null, null);
    }

    public SessionInfoResponse getActiveSession(Long counselorId) {
        return chatSessionRepository.findActiveSessionByCounselor(counselorId)
                .map(this::toResponse)
                .orElse(null);
    }

    private SessionInfoResponse toResponse(ChatSession s) {
        return new SessionInfoResponse(
                s.getId(),
                s.getStatus().name(),
                s.getUserId(),
                s.getCounselorId(),
                s.getCategoryId(),
                s.getStartedAt()
        );
    }

    // =========================================================
    // 2. 세션 종료(END)
    // =========================================================
    @Transactional
    public void endSession(Long sessionId, Long actorId, String reason) {

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        // 권한 체크
        validateAccess(session, actorId);

        // 이미 종료된 상태 확인
        validateNotFinished(session);

        // DB 처리
        endSessionFacade.endByUser(sessionId, session.getCounselorId());

        // WebSocket 알림
        eventService.sendEnd(sessionId, session.getCounselorId());

        log.info("[Service] Session END: sessionId={}, by actorId={}", sessionId, actorId);
    }

    // =========================================================
    // 3. 메시지 조회
    // =========================================================
    @Transactional(readOnly = true)
    public ChatSession getAndValidateSession(Long sessionId, Long actorId) {

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        validateAccess(session, actorId);

        return session;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long sessionId, Long actorId) {

        ChatSession session = getAndValidateSession(sessionId, actorId);

        List<ChatMessage> messages =
                chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);

        return messages.stream()
                .map(m -> new ChatMessageResponse(
                        m.getId(),
                        m.getSenderType(),
                        m.getSenderId(),
                        m.getMessage(),
                        m.getCreatedAt().toEpochMilli()
                ))
                .toList();
    }

    // =========================================================
    // 4. 상담사 수락(ACCEPT)
    // =========================================================
    @Transactional
    public void acceptSession(Long sessionId, Long counselorId) {

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        // 허용된 상담사인지 확인
        if (!counselorId.equals(session.getCounselorId())) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        // 종료 상태인지 확인
        validateNotFinished(session);

        // 상태 업데이트
        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setStartedAt(LocalDateTime.now());

        // Redis 반영
        redisTemplate.opsForValue().set(RedisKeyManager.sessionStatus(sessionId), "IN_PROGRESS");
        redisTemplate.opsForValue().set(RedisKeyManager.counselorStatus(counselorId), "BUSY");

        // WebSocket
        eventService.sendAccept(sessionId, counselorId);

        log.info("[Service] Session ACCEPT: sessionId={}, counselorId={}", sessionId, counselorId);
    }

    // =========================================================
    // 5. 세션 취소(CANCEL)
    // =========================================================
    @Transactional
    public void cancelSession(Long sessionId, Long actorId, String reason) {

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        validateAccess(session, actorId);
        validateNotFinished(session);

        // 상태 변경
        session.setStatus(SessionStatus.CANCELLED);
        session.setUpdatedAt(LocalDateTime.now());

        // Redis 변경
        redisTemplate.opsForValue().set(RedisKeyManager.sessionStatus(sessionId), "CANCELLED");

        // load 감소
        if (session.getCounselorId() != null) {
            redisTemplate.opsForValue().increment(
                    RedisKeyManager.counselorLoad(session.getCounselorId()), -1
            );

            redisTemplate.opsForValue()
                    .set(RedisKeyManager.counselorStatus(session.getCounselorId()), "AFTER_CALL");
        }

        String actorType = actorId.equals(session.getUserId()) ? "USER" : "COUNSELOR";
        eventService.sendCancel(sessionId, actorId, actorType);

        log.info("[Service] Session CANCELLED: sessionId={}, by actorId={}", sessionId, actorId);
    }

    // =========================================================
    // 6. 공통 검증 로직
    // =========================================================
    private void validateAccess(ChatSession session, Long actorId) {
        if (!actorId.equals(session.getUserId()) &&
                !actorId.equals(session.getCounselorId())) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }
    }

    private void validateNotFinished(ChatSession session) {
        if (session.getStatus() == SessionStatus.ENDED ||
                session.getStatus() == SessionStatus.CANCELLED) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_FINISHED);
        }
    }
}
