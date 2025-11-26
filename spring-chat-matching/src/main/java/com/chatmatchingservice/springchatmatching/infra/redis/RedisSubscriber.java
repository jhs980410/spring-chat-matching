package com.chatmatchingservice.springchatmatching.infra.redis;

import com.chatmatchingservice.springchatmatching.domain.chat.service.message.MessageHandler;
import com.chatmatchingservice.springchatmatching.domain.chat.websocket.MessageFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Redis Pub/Sub → WebSocket(STOMP) 브리지
 *
 * - Redis 채널(ws:session:*)에서 메시지를 받으면
 * - /sub/session/{sessionId} 로 STOMP 브로드캐스트
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final MessageFactory messageFactory;       //  추가
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            WSMessage payload = objectMapper.readValue(json, WSMessage.class);

            // ⭐ 내부 로직 처리 (핸들러 패턴)
            try {
                MessageHandler handler = messageFactory.getHandler(payload);
                handler.handle(payload);
            } catch (Exception e) {
                log.error("[WS][Subscriber] Handler 처리 실패: {}", e.getMessage());
            }

            // 기존 STOMP 브로드캐스트
            String dest = "/sub/session/" + payload.getSessionId();
            messagingTemplate.convertAndSend(dest, payload);

            log.debug("[RedisSubscriber] STOMP 전송 dest={}, payload={}", dest, payload);

        } catch (Exception e) {
            log.error("[RedisSubscriber] 메시지 처리 실패: {}", e.getMessage(), e);
        }
    }
}
