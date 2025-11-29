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
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.Map;

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
                case SEND -> handleSend(accessor);         // ⭐ 추가된 라인
                case DISCONNECT -> handleDisconnect(accessor);
            }

        } catch (CustomException e) {
            log.error("[WS][StompHandler] {} CustomException: code={}, msg={}",
                    command, e.getErrorCode().getCode(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("[WS][StompHandler] {} 처리 중 예상치 못한 예외: {}",
                    command, e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
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
                .getAuthority();

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

        // 프레임에 User 세팅
        accessor.setUser(principal);

        // 세션에도 저장
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null) {
            sessionAttributes.put("WS_PRINCIPAL", principal);
        }

        accessor.setLeaveMutable(true);

        log.info("[WS] CONNECT 성공: sessionId={}, principalId={}, role={}",
                accessor.getSessionId(), id, role);
    }

    // =====================================================
    // SUBSCRIBE — 세션ID 기반 권한 체크
    // =====================================================
    private void handleSubscribe(StompHeaderAccessor accessor) {

        Principal principal = restorePrincipal(accessor, StompCommand.SUBSCRIBE);

        String destination = accessor.getDestination();
        log.info("[FRAME] {} / sessionId={}, principal={}",
                accessor.getCommand(), accessor.getSessionId(), principal);

        if (!StringUtils.hasText(destination)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!destination.startsWith("/sub/session/")) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

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

        if ("USER".equals(role)) {
            if (!session.getUserId().equals(chatPrincipal.getId())) {
                throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
            }
        }

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
    // SEND — principal 복원 (핵심)
    // =====================================================
    private void handleSend(StompHeaderAccessor accessor) {

        Principal principal = restorePrincipal(accessor, StompCommand.SEND);

        if (principal == null) {
            log.warn("[WS] SEND 프레임 principal 없음: sessionId={}", accessor.getSessionId());
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        log.info("[WS] SEND principal OK: sessionId={}, principal={}",
                accessor.getSessionId(), principal.getName());
    }

    // =====================================================
    // DISCONNECT
    // =====================================================
    private void handleDisconnect(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        log.info("[WS] DISCONNECT: sessionId={}, principal={}",
                accessor.getSessionId(),
                principal != null ? principal.getName() : "null");
    }

    // =====================================================
    // 공통 principal 복원 로직
    // =====================================================
    private Principal restorePrincipal(StompHeaderAccessor accessor, StompCommand cmd) {

        Principal principal = accessor.getUser();

        if (principal == null) {
            Map<String, Object> attrs = accessor.getSessionAttributes();
            if (attrs != null) {
                Object saved = attrs.get("WS_PRINCIPAL");
                if (saved instanceof Principal) {
                    principal = (Principal) saved;
                    accessor.setUser(principal);
                    log.info("[WS] {} 시 principal 복원: sessionId={}, principal={}",
                            cmd, accessor.getSessionId(), principal.getName());
                }
            }
        }

        return principal;
    }

    private Long parseSessionId(String dest) {
        try {
            return Long.valueOf(dest.substring("/sub/session/".length()));
        } catch (Exception e) {
            return null;
        }
    }
}
