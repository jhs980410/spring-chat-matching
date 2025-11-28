package com.chatmatchingservice.springchatmatching.global.config;

import com.chatmatchingservice.springchatmatching.domain.chat.websocket.StompHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws/connect")
//                .setAllowedOriginPatterns(
//                        "http://localhost:5173",
//                        "http://localhost:5174",
//                        "https://*.o-r.kr"
//                )
                //로컬테스트용  전체 오픈
                .setAllowedOriginPatterns("*")
                .withSockJS();

        log.info("🔌 WebSocket STOMP Endpoint 등록 완료: /ws/connect");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");

        log.info("📡 STOMP MessageBroker 활성화: sub=/sub, pub=/pub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(stompHandler);

        log.info("🛡 STOMP Inbound Channel Interceptor 등록 완료 (StompHandler)");
    }
}
