package com.chatmatchingservice.springchatmatching.domain.chat.repository;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
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
                .build();

        return jpaRepository.save(session);
    }

    @Override
    public void assignCounselor(Long sessionId, long counselorId) {
        ChatSession session = jpaRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        session.setCounselorId(counselorId);
        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setUpdatedAt(LocalDateTime.now());

        jpaRepository.save(session);
    }
    @Override
    public void endSession(Long sessionId) {
        ChatSession session = jpaRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        session.setStatus(SessionStatus.ENDED);
        session.setUpdatedAt(LocalDateTime.now());

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

}
