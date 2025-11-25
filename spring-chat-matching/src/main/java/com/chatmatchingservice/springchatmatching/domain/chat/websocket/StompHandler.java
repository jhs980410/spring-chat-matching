package com.chatmatchingservice.springchatmatching.domain.chat.websocket;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.global.auth.ChatPrincipal;
import com.chatmatchingservice.springchatmatching.global.auth.jwt.JwtTokenProvider; // 이미 있는 JwtTokenProvider 사용 가정
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
import java.util.Optional;

/**
 * STOMP 핸들러
 *
 * - CONNECT: JWT 인증 후 ChatPrincipal 주입
 * - SUBSCRIBE: /sub/session/{sessionId} 권한 체크
 */
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

        if (command == null) {
            return message;
        }

        try {
            switch (command) {
                case CONNECT -> handleConnect(accessor);
                case SUBSCRIBE -> handleSubscribe(accessor);
                case DISCONNECT -> handleDisconnect(accessor);
                default -> { /* 기타 COMMAND는 특별 처리 없음 */ }
            }
        } catch (Exception e) {
            log.error("[WS][StompHandler] {} 처리 중 예외: {}", command, e.getMessage(), e);
            // 예외 발생 시 메시지 자체를 차단하고 싶다면 null 리턴
            // return null;
        }

        return message;
    }

    /**
     * CONNECT 시 JWT 인증 → ChatPrincipal 생성
     */
    private void handleConnect(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("[WS] Authorization 헤더 누락 또는 형식 오류");
            throw new IllegalArgumentException("JWT 토큰이 필요합니다.");
        }

        String token = authHeader.substring(7);

        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("[WS] 유효하지 않은 JWT 토큰");
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // Authentication에서 id, role 추출 (JwtTokenProvider 구현에 맞게 조정)
        var authentication = jwtTokenProvider.getAuthentication(token);
        String role = authentication.getAuthorities().iterator().next().getAuthority(); // ex) "ROLE_USER"
        if (role.startsWith("ROLE_")) {
            role = role.substring("ROLE_".length()); // "USER", "COUNSELOR" 형태로 정리
        }

        Long id = Long.valueOf(authentication.getName()); // name에 id를 넣었다고 가정 (필요 시 커스텀)

        ChatPrincipal principal = new ChatPrincipal(id, role);
        accessor.setUser(principal);

        log.info("[WS] CONNECT 성공: sessionId={}, principal={}", accessor.getSessionId(), principal.getName());
    }

    /**
     * /sub/session/{sessionId} 구독 요청 시 권한 체크
     */
    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination(); // ex) /sub/session/101

        if (!StringUtils.hasText(destination)) {
            log.warn("[WS] SUBSCRIBE dest 누락");
            throw new IllegalArgumentException("구독 경로가 필요합니다.");
        }

        if (!destination.startsWith("/sub/session/")) {
            log.warn("[WS] 허용되지 않은 구독 경로: {}", destination);
            throw new IllegalArgumentException("허용되지 않은 구독 경로입니다.");
        }

        Principal principal = accessor.getUser();
        if (!(principal instanceof ChatPrincipal chatPrincipal)) {
            log.warn("[WS] Principal이 ChatPrincipal이 아님: {}", principal);
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        Long sessionId = parseSessionId(destination);
        if (sessionId == null) {
            log.warn("[WS] 잘못된 sessionId: dest={}", destination);
            throw new IllegalArgumentException("잘못된 세션 ID입니다.");
        }

        // DB에서 세션 조회
        Optional<ChatSession> optionalSession = chatSessionRepository.findById(sessionId);
        if (optionalSession.isEmpty()) {
            log.warn("[WS] 존재하지 않는 세션: sessionId={}", sessionId);
            throw new IllegalArgumentException("존재하지 않는 세션입니다.");
        }

        ChatSession session = optionalSession.get();
        String role = chatPrincipal.getRole();

        // USER: 본인 세션만 구독 가능
        if ("USER".equals(role)) {
            if (!session.getUserId().equals(chatPrincipal.getId())) {
                log.warn("[WS] USER 세션 접근 권한 없음: userId={}, session.userId={}",
                        chatPrincipal.getId(), session.getUserId());
                throw new IllegalArgumentException("이 세션에 대한 권한이 없습니다.");
            }
        }

        // COUNSELOR: 배정된 세션만 구독 가능
        if ("COUNSELOR".equals(role)) {
            if (session.getCounselorId() == null ||
                    !session.getCounselorId().equals(chatPrincipal.getId())) {
                log.warn("[WS] COUNSELOR 세션 접근 권한 없음: counselorId={}, session.counselorId={}",
                        chatPrincipal.getId(), session.getCounselorId());
                throw new IllegalArgumentException("이 세션에 대한 권한이 없습니다.");
            }
        }

        log.info("[WS] SUBSCRIBE 허용: dest={}, principal={}", destination, chatPrincipal.getName());
    }

    /**
     * DISCONNECT 단순 로그 (추후 상담사 OFFLINE 처리에 사용 가능)
     */
    private void handleDisconnect(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        log.info("[WS] DISCONNECT: sessionId={}, principal={}",
                accessor.getSessionId(),
                principal != null ? principal.getName() : "null");
    }

    private Long parseSessionId(String dest) {
        try {
            String idStr = dest.substring("/sub/session/".length());
            return Long.valueOf(idStr);
        } catch (Exception e) {
            return null;
        }
    }
}
