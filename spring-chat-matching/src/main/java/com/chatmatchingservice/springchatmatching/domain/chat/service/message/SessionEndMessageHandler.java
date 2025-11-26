package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionEndMessageHandler implements MessageHandler {

    private final WebSocketPublisher publisher;

    @Override
    public void handle(WSMessage message) {
        try {
            log.info("[WS][Handler] SESSION_ENDED 처리: {}", message);

            // ================================
            // Validation
            // ================================
            if (message.getSessionId() == null || message.getSessionId().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            Long sessionId;
            try {
                sessionId = Long.valueOf(message.getSessionId());
            } catch (NumberFormatException e) {
                log.error("[WS][Handler] sessionId 변환 실패: {}", message.getSessionId());
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            // ================================
            // Publish 처리
            // ================================
            publisher.broadcast(sessionId, message);

        } catch (CustomException e) {
            // CustomException 은 그대로 rethrow
            log.error("[WS][Handler] CustomException 처리: {}", e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            // 예상 못한 예외 → INTERNAL_SERVER_ERROR
            log.error("[WS][Handler] SESSION_ENDED 처리 중 예상치 못한 오류 발생", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean supports(String type) {
        return "SESSION_ENDED".equals(type);
    }
}
