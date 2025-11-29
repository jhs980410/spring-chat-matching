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
                .setAllowedOriginPatterns("*");


        log.info("🔌 WebSocket STOMP Endpoint 등록 완료: /ws/connect");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

//        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");

        log.info("📡 STOMP MessageBroker 활성화: sub=/sub, pub=/pub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.taskExecutor()
                .corePoolSize(1)
                .maxPoolSize(1)
                .queueCapacity(1000);

        registration.interceptors(stompHandler);
        // ⭐ 핵심 조치: SecurityContext 전파 인터셉터 추가
           registration.interceptors(new SecurityContextChannelInterceptor());
        log.info("🛡 StompHandler + 단일 스레드 inbound 적용 완료");
    }

    //Spring Boot 3.x / Spring Messaging 6.x 환경에서 principal 유실 문제를 해결하는 “정식 솔루션”
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {

        // 최신 Spring Boot 방식
        registry.addDecoratorFactory(handler -> new WebSocketHandlerDecorator(handler) {
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
                    throws Exception {
                synchronized (session) {
                    super.handleMessage(session, message);
                }
            }
        });

        log.info("🔥 Transport Decorator 적용됨 (프레임 순서 보정 활성화)");
    }
}
