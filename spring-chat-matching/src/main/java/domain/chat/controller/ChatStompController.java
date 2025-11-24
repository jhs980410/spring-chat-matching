package domain.chat.controller;

import infra.redis.RedisKeyManager;
import infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final RedisTemplate<String, Object> redisTemplate;

    @MessageMapping("/session/{sessionId}")
    public void sendMessage(@Payload WSMessage message) {

        Long sessionId = Long.valueOf(message.getSessionId());

        String channel = RedisKeyManager.wsChannel(sessionId);
        redisTemplate.convertAndSend(channel, message);
    }
}
