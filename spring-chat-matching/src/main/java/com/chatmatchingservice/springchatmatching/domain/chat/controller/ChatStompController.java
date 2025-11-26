package com.chatmatchingservice.springchatmatching.domain.chat.controller;


import com.chatmatchingservice.springchatmatching.domain.chat.service.message.MessageHandler;
import com.chatmatchingservice.springchatmatching.domain.chat.websocket.MessageFactory;

import com.chatmatchingservice.springchatmatching.infra.redis.RedisPublisher;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatStompController {

    private final RedisPublisher redisPublisher;
    private final MessageFactory messageFactory;
    private final RedisRepository redisRepository;

    @MessageMapping("/session/{sessionId}")
    public void sendMessage(@DestinationVariable String sessionId,
                            @Payload WSMessage message,
                            Principal principal) {

        try {
            if (principal == null) {
                log.warn("[WS] Principal 없음, 메시지 무시: sessionId={}", sessionId);
                return;
            }

            if (message.getSessionId() == null || !sessionId.equals(message.getSessionId())) {
                log.warn("[WS] path 세션 ID와 payload 세션 ID 불일치: path={}, payload={}",
                        sessionId, message.getSessionId());
                return;
            }

            // 🔹 Principal → senderId / senderType 세팅
            Authentication auth = (Authentication) principal;
            Long senderId = (Long) auth.getPrincipal();  // JwtTokenProvider 에서 userId를 principal 로 넣었음

            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority().replace("ROLE_", "")) // ROLE_USER → USER
                    .orElse("USER");

            WSMessage enriched = new WSMessage(
                    message.getType(),
                    sessionId,
                    role,                       // senderType (USER / COUNSELOR)
                    senderId,
                    message.getMessage(),
                    message.getTimestamp() != null
                            ? message.getTimestamp()
                            : Instant.now().toEpochMilli()
            );

            // 1) Command / Handler 실행
            MessageHandler handler = messageFactory.getHandler(enriched);
            handler.handle(enriched);

            // 2) Redis Pub/Sub 브로드캐스트
            String channel = redisRepository.wsChannel(Long.valueOf(sessionId));
            redisPublisher.publish(channel, enriched);

        } catch (Exception e) {
            log.error("[WS] sendMessage 처리 중 예외: {}", e.getMessage(), e);
        }
    }
}
