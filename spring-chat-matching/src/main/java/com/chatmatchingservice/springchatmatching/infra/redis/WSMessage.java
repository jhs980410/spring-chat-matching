package com.chatmatchingservice.springchatmatching.infra.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * WebSocket & Redis Pub/Sub에서 공통으로 사용하는 메시지 포맷
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WSMessage {
    private String type;       // MESSAGE / ASSIGNED / SYSTEM 등
    private String sessionId;
    private String senderType; // USER / COUNSELOR / SYSTEM
    private Long senderId;
    private String message;
    private Long timestamp;
}
