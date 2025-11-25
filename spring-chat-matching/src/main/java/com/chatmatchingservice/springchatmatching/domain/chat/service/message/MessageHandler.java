package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;

/**
 * Command 패턴의 Command 역할
 * - 메시지 타입별로 다른 행위를 수행
 */
public interface MessageHandler {
    void handle(WSMessage message);
    boolean supports(String type);
}
