package com.chatmatchingservice.springchatmatching.global.config;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import com.chatmatchingservice.springchatmatching.domain.chat.websocket.StompHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/connect")
                .setAllowedOriginPatterns("*").withSockJS(); // CORS 허용

        log.info("🔌 WebSocket STOMP Endpoint 등록 완료: /ws/connect");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 🔥 [수정!] Simple Broker 제거:
        // Simple Broker는 Redis Pub/Sub과 기능이 중복되어 중복 메시지 전송을 유발합니다.
        // registry.enableSimpleBroker("/sub"); // <-- 이 라인을 제거합니다.

        // 📌 [추가] 외부 브로커 사용을 명시하거나, 아무것도 설정하지 않아 Simple Broker를 비활성화합니다.
        // 대신, RedisSubscriber가 SimpMessagingTemplate을 통해 직접 메시지를 /sub으로 발행합니다.

        // 클라이언트 메시지 → @MessageMapping("/session/...") 으로 전달
        registry.setApplicationDestinationPrefixes("/pub");

        log.info("📡 STOMP Broker 설정 완료: prefix=/pub (Simple Broker 비활성화됨)");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.taskExecutor()
                .corePoolSize(1)
                .maxPoolSize(1)
                .queueCapacity(1000);

        registration.interceptors(stompHandler);
        registration.interceptors(new SecurityContextChannelInterceptor());

        log.info("🛡 inboundChannel 설정 완료");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {

        registry.addDecoratorFactory(handler -> new WebSocketHandlerDecorator(handler) {
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
                    throws Exception {
                synchronized (session) {
                    super.handleMessage(session, message);
                }
            }
        });

        log.info("🔥 WebSocketTransport Decorator 적용됨");
    }
}
