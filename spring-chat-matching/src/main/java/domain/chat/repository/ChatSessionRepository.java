package domain.chat.repository;

import domain.chat.entity.ChatSession;

public interface ChatSessionRepository {

    ChatSession createWaitingSession(Long userId, Long categoryId);

    void assignCounselor(Long sessionId, long counselorId);
}
