package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 상담사 배정(ASSIGNED) 메시지 처리 핸들러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AssignedHandler implements MessageHandler {

    private final RedisRepository redisRepository;
    @Override
    public boolean supports(String type) {
        return "ASSIGNED".equalsIgnoreCase(type);
    }

    @Override
    public void handle(WSMessage message) {
        try {
            // ======================
            // 1) 기본 유효성 검사
            // ======================
            if (message == null) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            if (message.getSessionId() == null ||
                    message.getSessionId().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            // ======================
            // 2) 실제 메시지 처리
            // ======================
            log.info("[Handler][ASSIGNED] 상담사 배정 이벤트 처리: {}", message);

            String channel = "ws:session:" + message.getSessionId();
            redisRepository.publish(channel, message);

        } catch (CustomException e) {
            log.error("[Handler][ASSIGNED] CustomException 발생: {}", e.getErrorCode().getCode());
            throw e;  // 그대로 WebSocket 처리 계층으로 전달됨

        } catch (Exception e) {
            log.error("[Handler][ASSIGNED] 처리 중 예외 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
