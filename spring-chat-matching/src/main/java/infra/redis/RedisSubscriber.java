package infra.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Redis Pub/Sub → STOMP 전송
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            WSMessage payload = objectMapper.readValue(json, WSMessage.class);

            String dest = "/sub/session/" + payload.getSessionId();
            messagingTemplate.convertAndSend(dest, payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
