package com.chatmatchingservice.springchatmatching.domain.chat.repository;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionEndReason;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatSessionRepositoryImpl implements ChatSessionRepository {

    private final ChatSessionJpaRepository jpaRepository;
    @PersistenceContext
    private EntityManager em;
    @Override
    public ChatSession createWaitingSession(Long userId, Long categoryId,Long domainId) {
        ChatSession session = ChatSession.builder()
                .userId(userId)
                .categoryId(categoryId)
                .domainId(domainId)
                .status(SessionStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .requestedAt(LocalDateTime.now())
                .build();

        return jpaRepository.save(session);
    }

    @Override
    public void assignCounselor(Long sessionId, long counselorId) {
        ChatSession session = jpaRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        session.setCounselorId(counselorId);
        session.setAssignedAt(LocalDateTime.now());
        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setUpdatedAt(LocalDateTime.now());

        jpaRepository.save(session);
    }

    @Override
    public void endSession(Long sessionId) {
        endSession(sessionId, SessionEndReason.USER.name());
    }

    @Override
    public void endSession(Long sessionId, String endReason) {
        ChatSession session = jpaRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        LocalDateTime now = LocalDateTime.now();

        // 종료 시각
        session.setEndedAt(now);

        // 종료 사유 저장 (null이면 USER로 기본 설정)
        session.setEndReason(
                endReason != null
                        ? SessionEndReason.valueOf(endReason)
                        : SessionEndReason.USER
        );

        // 상담 시간 계산
        if (session.getStartedAt() != null) {
            long duration = java.time.Duration.between(session.getStartedAt(), now).getSeconds();
            session.setDurationSec(duration);
        } else {
            session.setDurationSec(0L);   // 시작 안 한 경우
        }

        // 상태 플래그 변경
        session.setStatus(SessionStatus.ENDED);
        session.setUpdatedAt(now);

        jpaRepository.save(session);
    }


    @Override
    public Optional<ChatSession> findById(Long sessionId) {
        return jpaRepository.findById(sessionId);
    }
    @Override
    public Optional<ChatSession> findActiveSessionByUser(Long userId) {
        return jpaRepository.findTopByUserIdAndStatusIn(
                userId,
                List.of(SessionStatus.WAITING, SessionStatus.IN_PROGRESS)
        );
    }

    @Override
    public Optional<ChatSession> findActiveSessionByCounselor(Long counselorId) {
        return jpaRepository.findTopByCounselorIdAndStatusIn(
                counselorId,
                List.of(SessionStatus.IN_PROGRESS, SessionStatus.AFTER_CALL)
        );
    }

    @Override
    public void markSessionStarted(Long sessionId) {
        ChatSession session = jpaRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        // 이미 시작 시간이 있다면 중복 저장 NO
        if (session.getStartedAt() != null) {
            return;
        }

        session.setStartedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        jpaRepository.save(session);
    }
    public Object[] findSessionDetail(Long sessionId) {

        String sql = """
        SELECT 
            s.id,
            s.status,
            u.id AS userId,
            u.nickname AS userName,
            u.email AS userEmail,
            co.id AS counselorId,
            cu.nickname AS counselorName,
            d.name AS domainName,
            c.name AS categoryName,
            c.id AS categoryId,
            s.requested_at,
            s.assigned_at,
            s.started_at,
            s.ended_at,
            s.duration_sec
        FROM chat_session s
        JOIN app_user u ON s.user_id = u.id
        LEFT JOIN counselor co ON s.counselor_id = co.id
        LEFT JOIN app_user cu ON cu.id = co.id
        JOIN category c ON s.category_id = c.id
        JOIN domain d ON s.domain_id = d.id
        WHERE s.id = ?1
        LIMIT 1
    """;

        List<Object[]> result = em.createNativeQuery(sql)
                .setParameter(1, sessionId)
                .getResultList();

        if (result.isEmpty()) return null;
        return result.get(0);
    }

    public List<Object[]> findMessages(Long sessionId) {

        String sql = """
        SELECT 
            m.id,
            m.sender_type,
            m.sender_id,
            au.nickname AS senderName,
            m.message,
             DATE_FORMAT(m.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt
        FROM chat_message m
        LEFT JOIN app_user au ON m.sender_id = au.id
        WHERE m.session_id = ?1
        ORDER BY m.created_at ASC
    """;

        return em.createNativeQuery(sql)
                .setParameter(1, sessionId)
                .getResultList();
    }
    @Override
    public Object[] findAfterCall(Long sessionId) {

        String sql = """
        SELECT satisfaction_score, after_call_sec, feedback, ended_at
        FROM counsel_log
        WHERE session_id = ?1
        LIMIT 1
    """;

        List<Object[]> list = em.createNativeQuery(sql)
                .setParameter(1, sessionId)
                .getResultList();

        return list.isEmpty() ? null : list.get(0);
    }

    // ==============================================
    // 🔥 상담사 개인 히스토리
    // ==============================================

    @Override
    public List<Object[]> findHistoryOfCounselor(Long counselorId) {

        // 문자열 분리!
        String sql =
                new StringBuilder().append("SELECT ").append("   s.id, ").append("   s.status, ").append("   u.id AS userId, ").append("   u.nickname AS userName, ").append("   co.id AS counselorId, ").append("   cu.nickname AS counselorName, ").append("   d.name AS domainName, ").append("   c.name AS categoryName, ").append("   s.requested_at, ").append("   s.started_at, ").append("   s.ended_at, ").append("   s.duration_sec ").append("FROM chat_session s ").append("JOIN app_user u ON s.user_id = u.id ").append("LEFT JOIN counselor co ON s.counselor_id = co.id ").append("LEFT JOIN app_user cu ON cu.id = co.id ").append("JOIN category c ON s.category_id = c.id ").append("JOIN domain d ON s.domain_id = d.id ").append("WHERE s.counselor_id = ").append(":counselorId").append(" ").append("ORDER BY s.requested_at DESC").toString();

        return em.createNativeQuery(sql)
                .setParameter("counselorId", counselorId)
                .getResultList();
    }


    // ==============================================
    // 🔥 전체 히스토리(관리자)
    // ==============================================
    @Override
    public List<Object[]> findAllHistory() {

        String sql = """
            SELECT 
                s.id,
                s.status,

                u.id AS userId,
                u.nickname AS userName,

                co.id AS counselorId,
                cu.nickname AS counselorName,

                d.name AS domainName,
                c.name AS categoryName,

                s.requested_at,
                s.started_at,
                s.ended_at,
                s.duration_sec
            FROM chat_session s
            JOIN app_user u ON s.user_id = u.id
            LEFT JOIN counselor co ON s.counselor_id = co.id
            LEFT JOIN app_user cu ON cu.id = co.id
            JOIN category c ON s.category_id = c.id
            JOIN domain d ON s.domain_id = d.id
            ORDER BY s.requested_at DESC
        """;

        return em.createNativeQuery(sql)
                .getResultList();
    }


    @Override
    public Long findActiveSessionId(Long counselorId) {
        String sql = """
        SELECT s.id
        FROM chat_session s
        WHERE s.counselor_id = :counselorId
          AND s.status IN ('IN_PROGRESS', 'AFTER_CALL')
        LIMIT 1
    """;

        List<Object> list = em.createNativeQuery(sql)
                .setParameter("counselorId", counselorId)
                .getResultList();

        if (list.isEmpty()) return null;
        return ((Number) list.get(0)).longValue();
    }

}
