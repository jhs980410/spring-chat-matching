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
public class EndHandler implements MessageHandler {

    private final RedisRepository redisRepository;

    @Override
    public boolean supports(String type) {
        return "END".equalsIgnoreCase(type);
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
            // 2) 메시지 처리
            // ======================
            log.info("[Handler][END] 상담 종료 처리: {}", message);

            String channel = "ws:session:" + message.getSessionId();
            redisRepository.publish(channel, message);

        } catch (CustomException e) {
            // 이미 정의된 CustomException이면 그대로 던짐
            log.error("[Handler][END] CustomException 발생: {}", e.getErrorCode().getCode());
            throw e;

        } catch (Exception e) {
            // 예상 못한 에러 → INTERNAL_SERVER_ERROR
            log.error("[Handler][END] 처리 중 비정상 예외 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
