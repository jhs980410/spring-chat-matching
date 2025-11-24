package domain.chat.repository;

import domain.chat.entity.ChatSession;
import domain.chat.entity.SessionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ChatSessionRepositoryImpl implements ChatSessionRepository {

    private final ChatSessionJpaRepository jpaRepository;

    @Override
    public ChatSession createWaitingSession(String sessionId, Long userId, Long categoryId) {

        ChatSession session = ChatSession.builder()
                .userId(userId)
                .categoryId(categoryId)
                .status(SessionStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return jpaRepository.save(session);
    }

    @Override
    public void assignCounselor(String sessionId, long counselorId) {
        Long id = Long.parseLong(sessionId);

        ChatSession session = jpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        session.setCounselorId(counselorId);
        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setUpdatedAt(LocalDateTime.now());

        jpaRepository.save(session);
    }
}
