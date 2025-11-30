package com.chatmatchingservice.springchatmatching.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class DisconnectNotice {
    private String type = "USER_DISCONNECTED";
    private Long sessionId;
    private Long userId;
    private DisconnectNotice(Long sessionId, Long userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public static DisconnectNotice of(Long sessionId, Long userId) {
        return new DisconnectNotice(sessionId, userId);
    }

}