package com.chatmatchingservice.springchatmatching.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis Pub/Sub Publisher
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String channel, Object message) {
        try {
            // 🔥 객체 자체를 그대로 Redis Pub/Sub 으로 보냄
            redisTemplate.convertAndSend(channel, message);

        } catch (Exception e) {
            log.error("[RedisPublisher] publish 실패: channel={}, error={}", channel, e.getMessage(), e);
        }
    }
}
