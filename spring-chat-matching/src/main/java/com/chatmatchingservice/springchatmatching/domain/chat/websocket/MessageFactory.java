package com.chatmatchingservice.springchatmatching.domain.chat.websocket;

import com.chatmatchingservice.springchatmatching.domain.chat.service.message.MessageHandler;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WebSocket 메시지 타입(type)에 따라
 * 적절한 MessageHandler 구현체를 찾아주는 Factory 클래스.
 *
 * - Factory Method + Command 패턴 결합
 * - 향후 새로운 type이 추가되면, 새 Handler만 추가하면 됨
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageFactory {

    /**
     * Spring 이 자동으로 주입해주는 모든 MessageHandler 구현체 목록
     * (ex: MessageSendHandler, EndSessionHandler 등)
     */
    private final List<MessageHandler> handlers;

    /**
     * WSMessage 의 type 에 맞는 핸들러를 찾아 반환
     *
     * @param message 수신된 웹소켓 메시지
     * @return 해당 type 을 처리할 수 있는 MessageHandler
     * @throws IllegalArgumentException 지원되지 않는 type 인 경우
     */
    public MessageHandler getHandler(WSMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("WSMessage 가 null 입니다.");
        }

        String type = message.getType();
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("WSMessage.type 이 비어 있습니다.");
        }

        // 등록된 핸들러 목록 중에서 supports(type) == true 인 구현체를 찾음
        return handlers.stream()
                .filter(handler -> {
                    try {
                        return handler.supports(type);
                    } catch (Exception e) {
                        log.error("[WS][Factory] handler.supports 중 예외 발생: handler={}", handler.getClass().getSimpleName(), e);
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("[WS][Factory] 지원하지 않는 메시지 타입: {}", type);
                    return new IllegalArgumentException("지원하지 않는 메시지 타입: " + type);
                });
    }
}
