package com.chatmatchingservice.springchatmatching.domain.chat.websocket;

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
 * - 상담사 온라인/오프라인 처리, 세션 정리 등에 활용 가능
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    // 필요하다면 CounselorStatusService, MatchingService 등을 주입 가능
    // private final CounselorStatusService counselorStatusService;

    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String sessionId = accessor.getSessionId();
            log.info("[WS-Event] CONNECT: sessionId={}", sessionId);

            // TODO: 헤더에서 역할/사용자 구분 → 상담사면 ONLINE 처리 등 응용 가능
        } catch (Exception e) {
            log.error("[WS-Event] CONNECT 처리 중 예외: {}", e.getMessage(), e);
        }
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        try {
            String sessionId = event.getSessionId();
            log.info("[WS-Event] DISCONNECT: sessionId={}", sessionId);

            // TODO: 세션과 counselorId/userId 매핑 관리한다면
            //      여기서 상담사 OFFLINE, 세션 정리 등의 로직을 호출할 수 있음.
        } catch (Exception e) {
            log.error("[WS-Event] DISCONNECT 처리 중 예외: {}", e.getMessage(), e);
        }
    }
}
