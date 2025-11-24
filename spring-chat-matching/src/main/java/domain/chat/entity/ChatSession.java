package domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_session")
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long counselorId;
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;  // WAITING / IN_PROGRESS / CLOSED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ChatSession createWaiting(Long userId, Long categoryId) {
        return ChatSession.builder()
                .userId(userId)
                .categoryId(categoryId)
                .status(SessionStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void assignCounselor(Long counselorId) {
        this.counselorId = counselorId;
        this.status = SessionStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }
}
