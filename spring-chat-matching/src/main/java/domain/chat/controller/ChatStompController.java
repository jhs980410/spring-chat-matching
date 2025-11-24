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

    // 클라이언트가 /pub/session/{sessionId} 로 메시지 보낼 때
    @MessageMapping("/session/{sessionId}")
    public void sendMessage(
            @Payload WSMessage message
    ) {
        // Redis Pub/Sub으로 publish
        String channel = RedisKeyManager.wsChannel(message.getSessionId());
        redisTemplate.convertAndSend(channel, message);
    }
}
