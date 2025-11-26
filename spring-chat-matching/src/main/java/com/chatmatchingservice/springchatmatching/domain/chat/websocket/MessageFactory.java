package com.chatmatchingservice.springchatmatching.domain.chat.websocket;

import com.chatmatchingservice.springchatmatching.domain.chat.service.message.MessageHandler;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageFactory {

    /** 모든 MessageHandler 구현체 자동 주입 */
    private final List<MessageHandler> handlers;

    /**
     * WSMessage 의 type 에 맞는 Handler 반환
     */
    public MessageHandler getHandler(WSMessage message) {

        // ================================
        // Validation
        // ================================
        if (message == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String type = message.getType();

        if (type == null || type.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // ================================
        // Handler 탐색
        // ================================
        return handlers.stream()
                .filter(handler -> {
                    try {
                        return handler.supports(type);
                    } catch (Exception e) {
                        log.error("[WS][Factory] handler.supports 예외 발생: handler={}",
                                handler.getClass().getSimpleName(), e);
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("[WS][Factory] 지원되지 않는 메시지 타입: {}", type);
                    return new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                });
    }
}
