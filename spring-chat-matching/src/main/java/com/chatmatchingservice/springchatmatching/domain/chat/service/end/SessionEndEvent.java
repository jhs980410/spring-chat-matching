package com.chatmatchingservice.springchatmatching.domain.chat.service.end;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SessionEndEvent {

    private final String type;       // e.g. "SESSION_ENDED"
    private final Long sessionId;
    private final Long counselorId;
    private final Long userId;
    private final long endedAt;     // epoch millis

    @Builder
    private SessionEndEvent(String type, Long sessionId, Long counselorId, Long userId, long endedAt) {
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
