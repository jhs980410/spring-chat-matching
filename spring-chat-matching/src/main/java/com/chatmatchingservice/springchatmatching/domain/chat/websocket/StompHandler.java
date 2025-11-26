package com.chatmatchingservice.springchatmatching.domain.chat.websocket;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.global.auth.ChatPrincipal;
import com.chatmatchingservice.springchatmatching.global.auth.jwt.JwtTokenProvider;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatSessionRepository chatSessionRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (command == null) return message;

        try {
            switch (command) {
                case CONNECT -> handleConnect(accessor);
                case SUBSCRIBE -> handleSubscribe(accessor);
                case DISCONNECT -> handleDisconnect(accessor);
            }

        } catch (CustomException e) {
            log.error("[WS][StompHandler] {} CustomException: code={}, msg={}",
                    command, e.getErrorCode().getCode(), e.getMessage());
            throw e; // WebSocket 연결 즉시 종료

        } catch (Exception e) {
            log.error("[WS][StompHandler] {} 처리 중 예상치 못한 예외: {}",
                    command, e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return message;
    }

    // =====================================================
    // CONNECT — JWT 인증
    // =====================================================
    private void handleConnect(StompHeaderAccessor accessor) {

        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        if (!jwtTokenProvider.validateToken(token)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        var authentication = jwtTokenProvider.getAuthentication(token);
        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority(); // ROLE_USER, ROLE_COUNSELOR

        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        Long id;
        try {
            id = Long.valueOf(authentication.getName());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        ChatPrincipal principal = new ChatPrincipal(id, role);
        accessor.setUser(principal);

        log.info("[WS] CONNECT 성공: principalId={}, role={}", id, role);
    }

    // =====================================================
    // SUBSCRIBE — 세션ID 기반 권한 체크
    // =====================================================
    private void handleSubscribe(StompHeaderAccessor accessor) {

        String destination = accessor.getDestination();

        if (!StringUtils.hasText(destination)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!destination.startsWith("/sub/session/")) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Principal principal = accessor.getUser();

        if (!(principal instanceof ChatPrincipal chatPrincipal)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long sessionId = parseSessionId(destination);
        if (sessionId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        String role = chatPrincipal.getRole();

        // USER → 본인 세션만 구독 허용
        if ("USER".equals(role)) {
            if (!session.getUserId().equals(chatPrincipal.getId())) {
                throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
            }
        }

        // COUNSELOR → assigned 된 세션만 허용
        if ("COUNSELOR".equals(role)) {
            if (session.getCounselorId() == null ||
                    !session.getCounselorId().equals(chatPrincipal.getId())) {
                throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
            }
        }

        log.info("[WS] SUBSCRIBE 허용: sessionId={}, principal={}",
                sessionId, chatPrincipal.getId());
    }

    // =====================================================
    // DISCONNECT
    // =====================================================
    private void handleDisconnect(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        log.info("[WS] DISCONNECT: principal={}",
                principal != null ? principal.getName() : "null");
    }


    private Long parseSessionId(String dest) {
        try {
            return Long.valueOf(dest.substring("/sub/session/".length()));
        } catch (Exception e) {
            return null;
        }
    }
}
