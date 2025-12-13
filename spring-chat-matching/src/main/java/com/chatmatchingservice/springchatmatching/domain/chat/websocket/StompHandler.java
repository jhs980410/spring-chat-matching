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
            log.error("[WS][StompHandler] {} CustomException: code={}, msg={}",
                    command, e.getErrorCode().getCode(), e.getMessage());
            // ❗❗ throw 금지
        } catch (Exception e) {
            log.error("[WS][StompHandler] {} unexpected error",
                    command, e);
            // ❗❗ throw 금지
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

        log.info("[FRAME][SUBSCRIBE] wsSessionId={}, dest={}, principal={}",
                accessor.getSessionId(), destination, principal);

        // ===============================
        // 1️⃣ destination 없음 → 무시
        // ===============================
        if (!StringUtils.hasText(destination)) {
            log.warn("[WS][SUBSCRIBE] destination empty");
            return;
        }

        // ===============================
        // 2️⃣ 상담사 알림 채널 허용
        // ===============================
        if (destination.startsWith("/sub/counselor/")) {
            log.info("[WS][SUBSCRIBE] counselor channel allowed: {}", destination);
            return;
        }

        // ===============================
        // 3️⃣ 세션 채널만 검증
        // ===============================
        if (!destination.startsWith("/sub/session/")) {
            log.warn("[WS][SUBSCRIBE] invalid destination: {}", destination);
            return;
        }

        // ===============================
        // 4️⃣ principal 검증
        // ===============================
        if (!(principal instanceof ChatPrincipal chatPrincipal)) {
            log.warn("[WS][SUBSCRIBE] invalid principal: {}", principal);
            return;
        }

        // ===============================
        // 5️⃣ sessionId 파싱
        // ===============================
        Long sessionId = parseSessionId(destination);
        if (sessionId == null) {
            log.warn("[WS][SUBSCRIBE] invalid sessionId from dest={}", destination);
            return;
        }

        // ===============================
        // 6️⃣ 세션 조회
        // ===============================
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElse(null);

        if (session == null) {
            log.warn("[WS][SUBSCRIBE] session not found: {}", sessionId);
            return;
        }

        String role = chatPrincipal.getRole();

        // ===============================
        // 7️⃣ USER 권한 체크
        // ===============================
        if ("USER".equals(role)) {
            if (!session.getUserId().equals(chatPrincipal.getId())) {
                log.warn("[WS][SUBSCRIBE] USER access denied: sessionId={}, userId={}",
                        sessionId, chatPrincipal.getId());
                return;
            }
        }

        // ===============================
        // 8️⃣ COUNSELOR 권한 체크
        // ===============================
        if ("COUNSELOR".equals(role)) {

            if (session.getCounselorId() == null ||
                    !session.getCounselorId().equals(chatPrincipal.getId())) {
                log.warn("[WS][SUBSCRIBE] COUNSELOR access denied: sessionId={}, counselorId={}",
                        sessionId, chatPrincipal.getId());
                return;
            }

            // 상담 시작 시간 기록 (1회)
            if (session.getStartedAt() == null) {
                try {
                    chatSessionRepository.markSessionStarted(sessionId);
                    log.info("[WS] 상담 시작 시간 기록 완료: sessionId={}, counselorId={}",
                            sessionId, chatPrincipal.getId());
                } catch (Exception e) {
                    log.error("[WS] started_at 저장 실패: sessionId={}, err={}",
                            sessionId, e.getMessage());
                }
            }
        }

        // ===============================
        // 9️⃣ 최종 허용 로그
        // ===============================
        log.info("[WS][SUBSCRIBE] 허용 완료: sessionId={}, principalId={}, role={}",
                sessionId, chatPrincipal.getId(), role);
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

        Principal principal = restorePrincipal(accessor, StompCommand.DISCONNECT);
        String wsSessionId = accessor.getSessionId();

        log.info("[WS] DISCONNECT 감지: wsSessionId={}, principal={}",
                wsSessionId, principal);

        if (!(principal instanceof ChatPrincipal chatPrincipal)) {
            return; // 웹소켓 연결만 하고 SUBSCRIBE 안 한 경우
        }

        Long userId = chatPrincipal.getId();
        String role = chatPrincipal.getRole();

        // 1) 고객만 disconnect 감지 처리 (상담사는 무시)
        if ("USER".equals(role)) {

            // 🔥 Redis에 disconnect timestamp 저장
            redisRepository.setUserDisconnectTime(userId, System.currentTimeMillis());

            // 해당 유저가 참여한 세션 ID 조회
            Long sessionId = redisRepository.getActiveSessionIdByUser(userId);
            if (sessionId != null) {

                // 상담사에게 “유저 이탈” 이벤트 발행
                DisconnectNotice notice = DisconnectNotice.of(sessionId, userId);

                redisRepository.publishToWsChannel(sessionId, notice);

                log.warn("[WS] USER disconnect → 상담사에게 전달 완료: sessionId={}, userId={}",
                        sessionId, userId);
            }
        }
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
