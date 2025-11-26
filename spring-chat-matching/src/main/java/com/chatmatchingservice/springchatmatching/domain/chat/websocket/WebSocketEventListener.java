package com.chatmatchingservice.springchatmatching.domain.chat.websocket;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocket 세션 이벤트 리스너
 *
 * - 실제 소켓 연결/해제 이벤트를 감지
 * - 상담사 ONLINE/OFFLINE 처리, 세션 정리 등 확장 가능
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String sessionId = accessor.getSessionId();

            if (sessionId == null) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            log.info("[WS-Event] CONNECT: sessionId={}", sessionId);

            // TODO: CONNECT 시 UserId/Role 추출하여 ONLINE 처리 가능

        } catch (CustomException e) {
            log.error("[WS-Event] CONNECT CustomException: code={}, msg={}",
                    e.getErrorCode().getCode(), e.getMessage());

        } catch (Exception e) {
            log.error("[WS-Event] CONNECT 처리 중 예외: {}", e.getMessage(), e);
        }
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        try {
            String sessionId = event.getSessionId();

            if (sessionId == null) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            log.info("[WS-Event] DISCONNECT: sessionId={}", sessionId);

            // TODO: sessionId → counselorId 매핑 관리 시
            //      상담사 OFFLINE 처리, 세션 종료 트리거 등 가능

        } catch (CustomException e) {
            log.error("[WS-Event] DISCONNECT CustomException: code={}, msg={}",
                    e.getErrorCode().getCode(), e.getMessage());

        } catch (Exception e) {
            log.error("[WS-Event] DISCONNECT 처리 중 예외: {}", e.getMessage(), e);
        }
    }
}
