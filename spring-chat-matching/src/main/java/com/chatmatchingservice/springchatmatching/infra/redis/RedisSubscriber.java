package com.chatmatchingservice.springchatmatching.infra.redis;

import com.chatmatchingservice.springchatmatching.domain.chat.service.message.MessageHandler;
import com.chatmatchingservice.springchatmatching.domain.chat.websocket.MessageFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Redis Pub/Sub → WebSocket(STOMP) 브리지
 *
 * - Redis 채널(ws:session:*)에서 메시지를 받으면
 * - /sub/session/{sessionId} 로 STOMP 브로드캐스트
 */

@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    @PostConstruct
    public void init() {
        log.warn("🔥 RedisSubscriber Bean 생성됨!");
    }
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 🔥 RedisTemplate의 ValueSerializer로 역직렬화
            Object deserialized = redisTemplate.getValueSerializer()
                    .deserialize(message.getBody());

            if (!(deserialized instanceof WSMessage payload)) {
                log.error("[RedisSubscriber] 역직렬화 실패: payload 타입이 WSMessage가 아님: {}", deserialized);
                return;
            }

            String dest = "/sub/session/" + payload.getSessionId();
            messagingTemplate.convertAndSend(dest, payload);

            log.debug("[RedisSubscriber] STOMP 전송 dest={}, payload={}", dest, payload);

        } catch (Exception e) {
            log.error("[RedisSubscriber] 메시지 처리 실패: {}", e.getMessage(), e);
        }
    }
}