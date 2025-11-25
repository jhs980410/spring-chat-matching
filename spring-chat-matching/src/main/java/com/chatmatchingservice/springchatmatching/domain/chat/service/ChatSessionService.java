package com.chatmatchingservice.springchatmatching.domain.chat.service;

import com.chatmatchingservice.springchatmatching.domain.chat.dto.ChatMessageResponse;
import com.chatmatchingservice.springchatmatching.domain.chat.dto.SessionInfoResponse;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatMessage;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatMessageRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.service.end.EndSessionFacade;
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
    public SessionInfoResponse getSessionOfUserOrCounselor(Long id) {

        // 1) 유저 기준 먼저 찾기
        Optional<ChatSession> userSession =
                chatSessionRepository.findActiveSessionByUser(id);

        if (userSession.isPresent()) {
            ChatSession s = userSession.get();
            return toResponse(s);
        }

        // 2) 상담사 기준
        Optional<ChatSession> counselorSession =
                chatSessionRepository.findActiveSessionByCounselor(id);

        if (counselorSession.isPresent()) {
            ChatSession s = counselorSession.get();
            return toResponse(s);
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

    /**
     * 세션 종료 API 핵심 로직 (컨트롤러에서 호출)
     */
    @Transactional
    public void endSession(Long sessionId, Long actorId, String reason) {

        // 1) 세션 존재 여부 확인
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        // 2) 종료 권한 확인
        if (!actorId.equals(session.getUserId()) &&
                !actorId.equals(session.getCounselorId())) {
            throw new SecurityException("이 세션을 종료할 권한이 없습니다.");
        }

        // 3) 종료 처리 (Facade로 위임)
        endSessionFacade.endByUser(sessionId, session.getCounselorId());
    }
    @Transactional(readOnly = true)
    public ChatSession getAndValidateSession(Long sessionId, Long actorId) {
        ChatSession s = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        // 접근 권한 검사 (userId or counselorId 중 하나여야 함)
        if (!actorId.equals(s.getUserId()) &&
                !actorId.equals(s.getCounselorId())) {
            throw new SecurityException("세션 접근 권한이 없습니다.");
        }

        return s;
    }
    /** 메시지 조회 */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long sessionId, Long actorId) {

        // 1) 접근 권한 체크
        ChatSession session = getAndValidateSession(sessionId, actorId);

        // 2) 메시지 조회
        List<ChatMessage> messages =
                chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());

        // 3) DTO 변환
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


    // =========================
    // 4. 상담사 측 세션 수락
    // =========================

    @Transactional
    public void acceptSession(Long sessionId, Long counselorId) {

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        // 🔹 상담사 권한 확인
        if (!counselorId.equals(session.getCounselorId())) {
            throw new SecurityException("본인의 상담 세션만 수락할 수 있습니다.");
        }

        // 🔹 상태 업데이트 (JPA 더티 체킹으로 반영 → save() 안 써도 됨)
        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setStartedAt(LocalDateTime.now());

        // 🔹 Redis 상태도 보정
        redisTemplate.opsForValue().set(
                RedisKeyManager.sessionStatus(sessionId), "IN_PROGRESS"
        );

        redisTemplate.opsForValue().set(
                RedisKeyManager.counselorStatus(counselorId), "BUSY"
        );

        // load는 MatchingService에서 이미 올려놨다고 가정
        // 필요하면 여기서도 확인/보정 가능

        // 로그
        System.out.printf("[Service] Session accepted: sessionId=%d, counselorId=%d%n",
                sessionId, counselorId);
    }

    @Transactional
    public void cancelSession(Long sessionId, Long actorId, String reason) {

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        // 🔹 종료 권한(유저/상담사) 체크
        if (!actorId.equals(session.getUserId()) &&
                !actorId.equals(session.getCounselorId())) {
            throw new SecurityException("세션 취소 권한이 없습니다.");
        }

        // 🔹 상태 업데이트
        session.setStatus(SessionStatus.CANCELLED);
        session.setUpdatedAt(LocalDateTime.now());

        // 🔹 Redis 상태 보정
        redisTemplate.opsForValue().set(
                RedisKeyManager.sessionStatus(sessionId),
                "CANCELLED"
        );

        // 🔹 상담사의 Load 조정 (배정된 경우만)
        if (session.getCounselorId() != null) {
            redisTemplate.opsForValue().increment(
                    RedisKeyManager.counselorLoad(session.getCounselorId()),
                    -1
            );

            // 상담사 상태 → AFTER_CALL
            redisTemplate.opsForValue().set(
                    RedisKeyManager.counselorStatus(session.getCounselorId()),
                    "AFTER_CALL"
            );
        }

        log.info("[Service] Session CANCELLED: sessionId={}, by actorId={}", sessionId, actorId);
    }

}
