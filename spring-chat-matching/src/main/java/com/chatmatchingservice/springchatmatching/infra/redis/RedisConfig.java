package com.chatmatchingservice.springchatmatching.infra.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.Executors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @PostConstruct
    public void init() {
        log.warn("🔥 RedisConfig 초기화됨!");
    }

    // =========================================================
    // 1. Redis Connection Factories 분리
    // =========================================================

    /**
     * Pub/Sub Listener 전용 Connection Factory
     * - RedisMessageListenerContainer 전용
     * - SUBSCRIBE 블로킹 연결 담당
     */
    @Bean(name = "listenerConnectionFactory")
    public RedisConnectionFactory listenerConnectionFactory(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port
    ) {
        RedisStandaloneConfiguration serverConfig =
                new RedisStandaloneConfiguration(host, port);

        LettuceClientConfiguration clientConfig =
                LettuceClientConfiguration.builder().build();

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    /**
     * CRUD / Publish 전용 Connection Factory
     * - RedisTemplate 전용
     */
    @Bean(name = "templateConnectionFactory")
    public RedisConnectionFactory templateConnectionFactory(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port
    ) {
        RedisStandaloneConfiguration serverConfig =
                new RedisStandaloneConfiguration(host, port);

        return new LettuceConnectionFactory(serverConfig);
    }

    // =========================================================
    // 2. Redis Pub/Sub Listener Container (단 하나만 존재)
    // =========================================================

    /**
     * 🚨 Redis Pub/Sub 컨테이너
     * - 반드시 JVM 내 단 1개
     * - 모든 ws:session:* 채널 구독
     */
    @Bean(name = "redisPubSubContainer")
    public RedisMessageListenerContainer redisPubSubContainer(
            @Qualifier("listenerConnectionFactory") RedisConnectionFactory connectionFactory,
            RedisSubscriber subscriber
    ) {
        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);

        // Pub/Sub는 단일 스레드가 가장 안전
        container.setTaskExecutor(Executors.newSingleThreadExecutor());

        container.addMessageListener(
                subscriber,
                new PatternTopic("ws:session:*")
        );

        log.warn("✅ Redis Pub/Sub Container 생성 완료 (ws:session:*)");

        return container;
    }

    // =========================================================
    // 3. Redis Subscriber
    // =========================================================

    @Bean
    public RedisSubscriber redisSubscriber(
            ObjectMapper objectMapper, // ✅ 추가
            SimpMessagingTemplate messagingTemplate
    ) {
        // 기존의 redisTemplate 대신 objectMapper와 messagingTemplate을 전달합니다.
        return new RedisSubscriber(objectMapper, messagingTemplate);
    }

    // =========================================================
    // 4. Redis Templates (CRUD / Publish)
    // =========================================================

    /**
     * JSON 객체 저장 / Publish 용 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("templateConnectionFactory") RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        log.warn("🔥 redisTemplate 초기화됨!");
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer();

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 순수 String (상태, ID, 카운트 등) 저장용 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, String> redisStringTemplate(
            @Qualifier("templateConnectionFactory") RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        log.warn("🔥 redisStringTemplate 초기화됨!");
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
