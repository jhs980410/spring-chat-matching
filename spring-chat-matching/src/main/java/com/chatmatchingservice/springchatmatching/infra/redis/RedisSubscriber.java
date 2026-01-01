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

    private final ObjectMapper objectMapper; // JSON 파싱을 위해 추가
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 1. 데이터를 문자열로 읽음
            String jsonContent = new String(message.getBody(), StandardCharsets.UTF_8);

            // 2. 일단 JsonNode로 변환하여 sessionId만 추출
            var jsonNode = objectMapper.readTree(jsonContent);

            if (!jsonNode.has("sessionId")) {
                log.error("[RedisSubscriber] sessionId 필드가 없음: {}", jsonContent);
                return;
            }

            Long sessionId = jsonNode.get("sessionId").asLong();
            String dest = "/sub/session/" + sessionId;

            // 3. 특정 클래스로 형변환하지 않고 JSON 그대로 전달
            // 이렇게 하면 WSMessage든 SessionEndEvent든 모두 통과합니다.
            messagingTemplate.convertAndSend(dest, jsonNode);

            log.debug("[RedisSubscriber] 브로드캐스트 완료: dest={}", dest);

        } catch (Exception e) {
            log.error("[RedisSubscriber] 메시지 처리 실패: {}", e.getMessage());
        }
    }
}