package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcceptHandler implements MessageHandler {

    private final RedisPublisher redisPublisher;

    @Override
    public boolean supports(String type) {
        return "ACCEPT".equalsIgnoreCase(type);
    }

    @Override
    public void handle(WSMessage message) {

        try {
            // ================
            // 1) 기본 유효성 검사
            // ================
            if (message == null) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            if (message.getSessionId() == null ||
                    message.getSessionId().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            // ================
            // 2) 처리 로직
            // ================
            log.info("[Handler][ACCEPT] 상담 수락 처리: {}", message);

            String channel = "ws:session:" + message.getSessionId();

            redisPublisher.publish(channel, message);

        } catch (CustomException e) {
            // 이미 ErrorCode 있는 경우 그대로 throw
            log.error("[Handler][ACCEPT] CustomException 발생: {}", e.getErrorCode().getCode());
            throw e;

        } catch (Exception e) {
            // 예상치 못한 예외는 INTERNAL_SERVER_ERROR 로 처리
            log.error("[Handler][ACCEPT] 처리 중 예외 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
