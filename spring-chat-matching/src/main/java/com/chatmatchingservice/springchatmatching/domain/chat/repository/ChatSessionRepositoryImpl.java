package com.chatmatchingservice.springchatmatching.domain.chat.repository;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionEndReason;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatSessionRepositoryImpl implements ChatSessionRepository {

    private final ChatSessionJpaRepository jpaRepository;

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

}
