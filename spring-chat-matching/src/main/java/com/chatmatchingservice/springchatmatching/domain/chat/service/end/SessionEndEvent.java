package com.chatmatchingservice.springchatmatching.domain.chat.service.end;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Getter
@ToString
@NoArgsConstructor  // ★ Jackson용 기본 생성자
public class SessionEndEvent {

    private String type;
    private Long sessionId;
    private Long counselorId;
    private Long userId;
    private long endedAt;

    @Builder
    public SessionEndEvent(String type, Long sessionId, Long counselorId, Long userId, long endedAt) {
        this.type = type;
        this.sessionId = sessionId;
        this.counselorId = counselorId;
        this.userId = userId;
        this.endedAt = endedAt;
    }

    public static SessionEndEvent of(Long sessionId, Long counselorId, Long userId) {
        return SessionEndEvent.builder()
                .type("SESSION_ENDED")
                .sessionId(sessionId)
                .counselorId(counselorId)
                .userId(userId)
                .endedAt(System.currentTimeMillis())
                .build();
    }
}
