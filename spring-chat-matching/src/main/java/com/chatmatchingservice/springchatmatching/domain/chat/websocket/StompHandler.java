package com.chatmatchingservice.springchatmatching.domain.chat.websocket;

import com.chatmatchingservice.springchatmatching.domain.chat.dto.DisconnectNotice;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.global.auth.ChatPrincipal;
import com.chatmatchingservice.springchatmatching.global.auth.jwt.JwtTokenProvider;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
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
    private final RedisRepository redisRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (command == null) return message;

        try {
            switch (command) {
                case CONNECT -> handleConnect(accessor);
                case SUBSCRIBE -> handleSubscribe(accessor);
                case SEND -> handleSend(accessor);
                case DISCONNECT -> handleDisconnect(accessor);
            }
        } catch (CustomException e) {
            log.error("[WS][{}] {}", command, e.getMessage());
        } catch (Exception e) {
            log.error("[WS][{}] unexpected error", command, e);
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

    // =====================================================
    // CONNECT — JWT 인증 (ChatPrincipal만 사용)
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

        // 🔥 핵심: ChatPrincipal만 생성
        ChatPrincipal chatPrincipal = new ChatPrincipal(id, role);

        // 🔥 STOMP User = ChatPrincipal
        accessor.setUser(chatPrincipal);

        // 🔥 STOMP 세션에도 ChatPrincipal만 저장
        Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
        if (sessionAttrs != null) {
            sessionAttrs.put("WS_PRINCIPAL", chatPrincipal);
        }

        accessor.setLeaveMutable(true);

        log.info("[WS] CONNECT 성공: wsSessionId={}, principal={}",
                accessor.getSessionId(), chatPrincipal.getName());
    }

    // =====================================================
    // SUBSCRIBE — 세션 접근 권한 검증
    // =====================================================
    private void handleSubscribe(StompHeaderAccessor accessor) {

        ChatPrincipal principal = restoreChatPrincipal(accessor, StompCommand.SUBSCRIBE);
        if (principal == null) return;

        String destination = accessor.getDestination();
        if (!StringUtils.hasText(destination)) return;

        if (destination.startsWith("/sub/counselor/")) return;
        if (!destination.startsWith("/sub/session/")) return;

        Long sessionId = parseSessionId(destination);
        if (sessionId == null) return;

        ChatSession session = chatSessionRepository.findById(sessionId).orElse(null);
        if (session == null) return;

        String role = principal.getRole();

        if ("USER".equals(role) && !session.getUserId().equals(principal.getId())) {
            log.warn("[WS][SUBSCRIBE] USER access denied");
            return;
        }

        if ("COUNSELOR".equals(role)) {
            if (session.getCounselorId() == null ||
                    !session.getCounselorId().equals(principal.getId())) {
                log.warn("[WS][SUBSCRIBE] COUNSELOR access denied");
                return;
            }

            if (session.getStartedAt() == null) {
                chatSessionRepository.markSessionStarted(sessionId);
            }
        }

        log.info("[WS][SUBSCRIBE] 허용: sessionId={}, principal={}",
                sessionId, principal.getName());
    }

    // =====================================================
    // SEND
    // =====================================================
    private void handleSend(StompHeaderAccessor accessor) {

        ChatPrincipal principal = restoreChatPrincipal(accessor, StompCommand.SEND);
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        log.info("[WS] SEND OK: wsSessionId={}, principal={}",
                accessor.getSessionId(), principal.getName());
    }

    // =====================================================
    // DISCONNECT
    // =====================================================
    private void handleDisconnect(StompHeaderAccessor accessor) {

        ChatPrincipal principal = restoreChatPrincipal(accessor, StompCommand.DISCONNECT);
        if (principal == null) return;

        if ("USER".equals(principal.getRole())) {
            redisRepository.setUserDisconnectTime(
                    principal.getId(),
                    System.currentTimeMillis()
            );

            Long sessionId = redisRepository.getActiveSessionIdByUser(principal.getId());
            if (sessionId != null) {
                redisRepository.publishToWsChannel(
                        sessionId,
                        DisconnectNotice.of(sessionId, principal.getId())
                );
            }
        }
    }

    // =====================================================
    // 공통: ChatPrincipal 복원
    // =====================================================
    private ChatPrincipal restoreChatPrincipal(StompHeaderAccessor accessor, StompCommand cmd) {

        Principal user = accessor.getUser();
        if (user instanceof ChatPrincipal cp) {
            return cp;
        }

        Map<String, Object> attrs = accessor.getSessionAttributes();
        if (attrs != null) {
            Object saved = attrs.get("WS_PRINCIPAL");
            if (saved instanceof ChatPrincipal cp) {
                accessor.setUser(cp);
                log.info("[WS] {} principal 복원: {}", cmd, cp.getName());
                return cp;
            }
        }

        log.warn("[WS] {} ChatPrincipal 없음", cmd);
        return null;
    }

    private Long parseSessionId(String dest) {
        try {
            return Long.valueOf(dest.substring("/sub/session/".length()));
        } catch (Exception e) {
            return null;
        }
    }
}
