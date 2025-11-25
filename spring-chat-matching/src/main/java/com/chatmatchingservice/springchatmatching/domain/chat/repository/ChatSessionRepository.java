package com.chatmatchingservice.springchatmatching.domain.chat.repository;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;

import java.util.Optional;

public interface ChatSessionRepository {

    ChatSession createWaitingSession(Long userId, Long categoryId);

    void assignCounselor(Long sessionId, long counselorId);
    void endSession(Long sessionId);

    Optional<ChatSession> findById(Long sessionId);
}
