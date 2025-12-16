package com.chatmatchingservice.springchatmatching.domain.chat.controller;

import com.chatmatchingservice.springchatmatching.domain.chat.service.message.MessageHandler;
import com.chatmatchingservice.springchatmatching.domain.chat.websocket.MessageFactory;
import com.chatmatchingservice.springchatmatching.global.auth.ChatPrincipal;

import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatStompController {

    private final MessageFactory messageFactory;
    private final RedisRepository redisRepository;

    @MessageMapping("/session/{sessionId}")
    public void sendMessage(
            @DestinationVariable String sessionId,
            @Payload WSMessage message,
            SimpMessageHeaderAccessor headerAccessor   // 🔥 핵심
    ) {
        try {
            /* ===============================
             * 1. 세션에서 ChatPrincipal 복원
             * =============================== */
            Map<String, Object> sessionAttrs = headerAccessor.getSessionAttributes();
            if (sessionAttrs == null) {
                log.error("[WS] sessionAttributes 없음");
                return;
            }

            Object saved = sessionAttrs.get("WS_PRINCIPAL");
            if (!(saved instanceof ChatPrincipal chatPrincipal)) {
                log.error(
                        "[WS] ChatPrincipal 없음 (session). actual={}",
                        saved != null ? saved.getClass() : "null"
                );
                return;
            }

            /* ===============================
             * 2. 세션 ID 검증
             * =============================== */
            if (message.getSessionId() == null || !sessionId.equals(message.getSessionId())) {
                log.warn(
                        "[WS] path 세션 ID와 payload 세션 ID 불일치: path={}, payload={}",
                        sessionId, message.getSessionId()
                );
                return;
            }

            Long senderId = chatPrincipal.getId();
            String role = chatPrincipal.getRole();

            /* ===============================
             * 3. 메시지 보강
             * =============================== */
            WSMessage enriched = new WSMessage(
                    message.getType(),
                    sessionId,
                    role,
                    senderId,
                    message.getMessage(),
                    message.getTimestamp() != null
                            ? message.getTimestamp()
                            : Instant.now().toEpochMilli()
            );

            /* ===============================
             * 4. 도메인 핸들러
             * =============================== */
            MessageHandler handler = messageFactory.getHandler(enriched);
            handler.handle(enriched);

            /* ===============================
             * 5. Redis Pub/Sub
             * =============================== */
            String channel = redisRepository.wsChannel(Long.valueOf(sessionId));
            redisRepository.publish(channel, enriched);

        } catch (Exception e) {
            log.error("[WS] sendMessage 처리 중 예외", e);
        }
    }
}
