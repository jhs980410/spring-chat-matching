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
                .setAllowedOriginPatterns("*"); // CORS 허용

        log.info("🔌 WebSocket STOMP Endpoint 등록 완료: /ws/connect");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 🔥 반드시 필요 — /sub 브로커 활성화
        registry.enableSimpleBroker("/sub");

        // 🔥 클라이언트 메시지 → @MessageMapping("/session/...") 으로 전달
        registry.setApplicationDestinationPrefixes("/pub");

        log.info("📡 STOMP Broker 설정 완료: enableSimpleBroker=/sub, prefix=/pub");
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
