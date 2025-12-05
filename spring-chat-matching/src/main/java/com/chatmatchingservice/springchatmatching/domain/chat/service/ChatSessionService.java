package com.chatmatchingservice.springchatmatching.domain.chat.service;

import com.chatmatchingservice.springchatmatching.domain.chat.dto.ChatMessageResponse;
import com.chatmatchingservice.springchatmatching.domain.chat.dto.SessionDetailResponse;
import com.chatmatchingservice.springchatmatching.domain.chat.dto.SessionHistoryResponse;
import com.chatmatchingservice.springchatmatching.domain.chat.dto.SessionInfoResponse;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatMessage;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatMessageRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.service.end.EndSessionFacade;

import com.chatmatchingservice.springchatmatching.domain.log.entity.CounselLog;
import com.chatmatchingservice.springchatmatching.domain.log.repository.CounselLogRepository;
import com.chatmatchingservice.springchatmatching.domain.user.entity.AppUser;
import com.chatmatchingservice.springchatmatching.domain.user.repository.AppUserRepository;

import com.chatmatchingservice.springchatmatching.domain.domain.entity.Domain;
import com.chatmatchingservice.springchatmatching.domain.domain.repository.DomainRepository;

import com.chatmatchingservice.springchatmatching.domain.category.entity.Category;
import com.chatmatchingservice.springchatmatching.domain.category.repository.CategoryRepository;


import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final AppUserRepository appUserRepository;
    private final DomainRepository domainRepository;
    private final CategoryRepository categoryRepository;
    private final CounselLogRepository counselLogRepository;

    private final EndSessionFacade endSessionFacade;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisRepository redisRepository;
    private final ChatSessionEventService eventService;


    public SessionInfoResponse convertToResponse(ChatSession session) {
        return toResponse(session);
    }

    // =========================================================
    // 1. 현재 진행 중인 세션 조회 (유저 or 상담사)
    // =========================================================
    @Transactional(readOnly = true)
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

        // 진행 중 세션 없음
        return SessionInfoResponse.empty();
    }


    // =========================================================
    // 2. 상담사 기준 진행 중 세션 조회
    // =========================================================
    @Transactional(readOnly = true)
    public SessionInfoResponse getActiveSession(Long counselorId) {
        return chatSessionRepository.findActiveSessionByCounselor(counselorId)
                .map(this::toResponse)
                .orElse(null);
    }


    // =========================================================
    // 3. 엔티티 → DTO 변환 (핵심)
    // =========================================================
    private SessionInfoResponse toResponse(ChatSession s) {

        // 1) 유저 정보
        AppUser user = appUserRepository.findById(s.getUserId())
                .orElseThrow();

        // 2) 도메인 정보
        Domain domain = domainRepository.findById(s.getDomainId())
                .orElseThrow();

        // 3) 카테고리 정보
        Category category = categoryRepository.findById(s.getCategoryId())
                .orElseThrow();

        // 4) 상담 로그 (after-call 정보)
        CounselLog log = counselLogRepository.findBySessionId(s.getId())
                .orElse(null);

        Integer satisfactionScore = (log != null) ? log.getSatisfactionScore() : null;
        Integer afterCallSec = (log != null) ? log.getAfterCallSec() : null;
        String feedback = (log != null) ? log.getFeedback() : null;

        return new SessionInfoResponse(
                s.getId(),
                s.getStatus().name(),

                user.getId(),
                user.getNickname(),
                user.getEmail(),

                s.getCounselorId(),

                s.getDomainId(),
                domain.getName(),

                s.getCategoryId(),
                category.getName(),

                s.getRequestedAt(),
                s.getStartedAt(),
                s.getEndedAt(),
                s.getDurationSec(),
                s.getEndReason(),

                satisfactionScore,
                afterCallSec,
                feedback
        );
    }


    // =========================================================
    // 4. 세션 종료(END)
    // =========================================================
    @Transactional
    public void endSession(Long sessionId, Long actorId, String reason) {

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        validateAccess(session, actorId);
        validateNotFinished(session);

        try {
            if (actorId.equals(session.getUserId())) {
                endSessionFacade.endByUser(sessionId, session.getUserId());
            } else {
                endSessionFacade.endByCounselor(sessionId, session.getCounselorId());
            }
        } catch (Exception e) {
            log.error("[END-ERROR] endSessionFacade ERROR", e);
            throw e;
        }

        try {
            eventService.sendEnd(sessionId, session.getCounselorId());
        } catch (Exception e) {
            log.error("[END-ERROR] eventService ERROR", e);
            throw e;
        }

        log.info("[Service] Session END: sessionId={}, by actorId={}", sessionId, actorId);
    }


    // =========================================================
    // 5. 메시지 조회
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
    // 6. 상담사 수락(ACCEPT)
    // =========================================================
    @Transactional
    public void acceptSession(Long sessionId, Long counselorId) {

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (!counselorId.equals(session.getCounselorId())) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        validateNotFinished(session);

        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setStartedAt(LocalDateTime.now());

        redisRepository.setSessionStatus(sessionId, "IN_PROGRESS");
        redisRepository.setCounselorStatus(counselorId, "BUSY");

        eventService.sendAccept(sessionId, counselorId);

        log.info("[Service] Session ACCEPT: sessionId={}, counselorId={}", sessionId, counselorId);
    }


    // =========================================================
    // 7. 세션 취소(CANCEL)
    // =========================================================
    @Transactional
    public void cancelSession(Long sessionId, Long actorId, String reason) {

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        validateAccess(session, actorId);
        validateNotFinished(session);

        session.setStatus(SessionStatus.CANCELLED);
        session.setUpdatedAt(LocalDateTime.now());

        redisRepository.setSessionStatus(sessionId, "CANCELLED");

        if (session.getCounselorId() != null) {
            redisRepository.incrementCounselorLoad(session.getCounselorId(), -1);
            redisRepository.setCounselorStatus(session.getCounselorId(), "AFTER_CALL");
        }

        String actorType = actorId.equals(session.getUserId()) ? "USER" : "COUNSELOR";
        eventService.sendCancel(sessionId, actorId, actorType);

        log.info("[Service] Session CANCELLED: sessionId={}, by actorId={}", sessionId, actorId);
    }


    // =========================================================
    // 8. 공통 검증
    // =========================================================
    private void validateAccess(ChatSession session, Long actorId) {

        // 🔸 현재 로그인된 사용자의 Role 확인
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isCounselor = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COUNSELOR"));

        // ============================
        // 1) ADMIN → 전체 접근 허용
        // ============================
        if (isAdmin) return;

        // ============================
        // 2) 상담사 → 전체 접근 허용 (READ ONLY)
        // ============================
        if (isCounselor) return;

        // ============================
        // 3) 고객 → 본인 세션만
        // ============================
        if (actorId.equals(session.getUserId())) return;

        // ============================
        // 접근 불가
        // ============================
        throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
    }

    private void validateNotFinished(ChatSession session) {
        if (session.getStatus() == SessionStatus.ENDED ||
                session.getStatus() == SessionStatus.CANCELLED) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_FINISHED);
        }
    }
    public SessionDetailResponse getSessionDetail(Long sessionId, Long actorId) {

        // 접근 권한 체크
        getAndValidateSession(sessionId, actorId);

        Object[] s = chatSessionRepository.findSessionDetail(sessionId);   // 단건
        List<Object[]> m = chatSessionRepository.findMessages(sessionId);  // 메시지 리스트
        Object[] a = chatSessionRepository.findAfterCall(sessionId);       // 단건 or null

        return SessionDetailResponse.of(s, m, a);
    }


    @Transactional(readOnly = true)
    public List<SessionHistoryResponse> getHistoryOfCounselor(Long counselorId) {

        List<Object[]> rows = chatSessionRepository.findHistoryOfCounselor(counselorId);

        return rows.stream().map(r -> new SessionHistoryResponse(
                toLong(r[0]),           // sessionId
                toStringVal(r[1]),      // status

                toLong(r[2]),           // userId
                toStringVal(r[3]),      // userName

                toLong(r[4]),           // counselorId
                toStringVal(r[5]),      // counselorName

                toStringVal(r[6]),      // domainName
                toStringVal(r[7]),      // categoryName

                toStringVal(r[8]),      // requestedAt
                toStringVal(r[9]),      // startedAt
                toStringVal(r[10]),     // endedAt
                toLong(r[11])           // durationSec
        )).toList();
    }

    @Transactional(readOnly = true)
    public List<SessionHistoryResponse> getAllHistory() {

        List<Object[]> rows = chatSessionRepository.findAllHistory();

        return rows.stream().map(r -> new SessionHistoryResponse(
                toLong(r[0]),           // sessionId
                toStringVal(r[1]),      // status

                toLong(r[2]),           // userId
                toStringVal(r[3]),      // userName

                toLong(r[4]),           // counselorId
                toStringVal(r[5]),      // counselorName

                toStringVal(r[6]),      // domainName
                toStringVal(r[7]),      // categoryName

                toStringVal(r[8]),      // requestedAt
                toStringVal(r[9]),      // startedAt
                toStringVal(r[10]),     // endedAt
                toLong(r[11])           // durationSec
        )).toList();
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        return Long.valueOf(o.toString());
    }

    private String toStringVal(Object o) {
        return o == null ? null : o.toString();
    }

}
