package com.chatmatchingservice.hqadmin.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 모든 API 경로에 대해
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:5176",
                        "http://13.209.214.254",
                        "https://jhs-platform.co.kr",
                        "https://customer.jhs-platform.co.kr",
                        "https://admin.jhs-platform.co.kr",
                        "https://manager.jhs-platform.co.kr",
                        "https://counselor.jhs-platform.co.kr"
                ) // 사진(image_6f9380.png)의 도메인들로 수정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // PATCH 추가 권장
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Set-Cookie", "X-MANAGER-ID") // 기존 헤더 유지 및 추가
                .allowCredentials(true);
    }
}