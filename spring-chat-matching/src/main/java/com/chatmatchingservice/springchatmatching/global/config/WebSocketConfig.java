package com.chatmatchingservice.springchatmatching.global.config;

import com.chatmatchingservice.springchatmatching.domain.chat.websocket.StompHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    // ================================
    // 1) WebSocket Endpoint 등록
    // ================================
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws/connect")
                .setAllowedOriginPatterns("*") // 로컬 전체 허용
                .withSockJS();

        log.info("🔌 WebSocket STOMP Endpoint 등록 완료: /ws/connect");
    }

    // ================================
    // 2) Message Broker 설정
    // ================================
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/sub"); // 구독 prefix
        registry.setApplicationDestinationPrefixes("/pub"); // 메시지 발행 prefix

        log.info("📡 STOMP MessageBroker 활성화: sub=/sub, pub=/pub");
    }

    // ================================
    // 3) Inbound Channel → StompHandler 적용
    // ================================
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(stompHandler);

        log.info("🛡 STOMP Inbound Channel Interceptor 등록 완료 (StompHandler)");
    }

    // ================================
    // ❗️4) Transport 설정 제거 (지원하지 않음)
    // ================================
    //  → setPreservePublishOrder() 는 네 Spring 버전에서 지원되지 않으므로 제거해야 함.
}
