package com.chatmatchingservice.springchatmatching.domain.chat.repository;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatSessionJpaRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findTopByUserIdAndStatusIn(Long userId, List<SessionStatus> statuses);
    Optional<ChatSession> findTopByCounselorIdAndStatusIn(Long counselorId, List<SessionStatus> statuses);
}
