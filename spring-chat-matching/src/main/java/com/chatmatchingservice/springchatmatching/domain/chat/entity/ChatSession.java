package com.chatmatchingservice.springchatmatching.domain.chat.entity;

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
    @Column(name = "domain_id")
    private Long domainId;
    @Enumerated(EnumType.STRING)
    private SessionStatus status;  // WAITING / IN_PROGRESS / AFTER_CALL / ENDED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 🔥 추가: DB 스키마에 존재하는 started_at 컬럼
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    // --- 생성 메서드 ---
    public static ChatSession createWaiting(Long userId, Long categoryId) {
        return ChatSession.builder()
                .userId(userId)
                .categoryId(categoryId)
                .status(SessionStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    public static ChatSession createWaiting(Long userId, Long categoryId, Long domainId) {
        return ChatSession.builder()
                .userId(userId)
                .categoryId(categoryId)
                .domainId(domainId) // <-- domainId 설정 추가
                .status(SessionStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // --- 상담사 배정 ---
    public void assignCounselor(Long counselorId) {
        this.counselorId = counselorId;
        this.status = SessionStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();  //  상담 시작 시간 기록
        this.updatedAt = LocalDateTime.now();
    }
}
