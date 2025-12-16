package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemHandler implements MessageHandler {

    private final RedisRepository redisRepository;

    @Override
    public boolean supports(String type) {
        return "SYSTEM".equalsIgnoreCase(type);
    }

    @Override
    public void handle(WSMessage message) {

        try {
            // -------------------------
            // 1) 유효성 검사
            // -------------------------
            if (message == null) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            if (message.getSessionId() == null || message.getSessionId().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            String channel = "ws:session:" + message.getSessionId();

            // -------------------------
            // 2) Redis 발행
            // -------------------------
            redisRepository.publish(channel, message);

            log.info("[Handler][SYSTEM] 시스템 메시지 처리 완료: sessionId={}, msg={}",
                    message.getSessionId(), message.getMessage());

        } catch (CustomException e) {
            log.error("[SYSTEM Handler] CustomException: code={}, msg={}",
                    e.getErrorCode().getCode(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("[SYSTEM Handler] 처리 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
