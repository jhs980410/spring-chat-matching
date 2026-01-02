package com.chatmatchingservice.springchatmatching.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // redis://127.0.0.1:6379 형태의 주소를 생성합니다.
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port);

        return Redisson.create(config);
    }
}