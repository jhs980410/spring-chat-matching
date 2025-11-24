package domain.chat.repository;

import domain.chat.entity.ChatSession;

public interface ChatSessionRepository {
    void assignCounselor(String sessionId, long counselorId);

    ChatSession createWaitingSession(String sessionId, Long userId, Long categoryId);

}