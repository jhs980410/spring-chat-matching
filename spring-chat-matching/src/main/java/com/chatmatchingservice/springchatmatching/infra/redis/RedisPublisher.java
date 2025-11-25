package com.chatmatchingservice.springchatmatching.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Observer 패턴에서 Subject 역할
 * - 특정 채널에 메시지를 발행(publish)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String channel, Object message) {
        try {
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            log.error("[RedisPublisher] publish 실패: channel={}, error={}", channel, e.getMessage(), e);
        }
    }
}
