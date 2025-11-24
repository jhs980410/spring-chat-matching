package infra.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WSMessage {
    private String type;       // MESSAGE / ASSIGNED 등
    private String sessionId;
    private String senderType; // USER / COUNSELOR
    private Long senderId;
    private String message;
    private Long timestamp;
}