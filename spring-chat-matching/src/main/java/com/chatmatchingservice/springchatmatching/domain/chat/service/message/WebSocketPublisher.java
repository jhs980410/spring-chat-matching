package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketPublisher {

    private final RedisRepository redisRepository;

    /**
     * 특정 sessionId 에 연결된 전체 참여자(user, counselor)에게 전송
     * - Redis Pub/Sub 의 channel 로 publish
     */
    public void broadcast(Long sessionId, WSMessage message) {
        try {
            if (sessionId == null || sessionId <= 0) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            if (message == null) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            String channel = redisRepository.wsChannel(sessionId);

            log.info("[WS][Publisher] Broadcasting to channel={} / message={}", channel, message);

            redisRepository.publishToWsChannel(sessionId, message);

        } catch (CustomException e) {
            // CustomException 은 그대로 전달
            log.error("[WS][Publisher] CustomException: {}", e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error("[WS][Publisher] 메시지 브로드캐스팅 중 오류 발생", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
