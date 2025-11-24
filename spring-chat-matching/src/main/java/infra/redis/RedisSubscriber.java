package infra.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * RedisMessageListenerAdapter 가 문자열 payload를 넘겨줄 때 사용하는 메서드
     */
    public void onMessage(String message, String channel) {
        try {
            // message를 WSMessage로 변환
            WSMessage payload = objectMapper.readValue(message, WSMessage.class);

            // 세션 구독 경로로 전달
            String dest = "/sub/session/" + payload.getSessionId();
            messagingTemplate.convertAndSend(dest, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
