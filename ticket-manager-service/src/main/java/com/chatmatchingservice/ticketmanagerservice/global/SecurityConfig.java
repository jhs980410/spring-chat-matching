package com.chatmatchingservice.ticketmanagerservice.global;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1) CORS 설정 추가
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. Swagger 및 API 문서 관련 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 2. 매니저 판매 계약 및 공연/티켓 Draft API 허용
                        .requestMatchers("/api/manager/**").permitAll()

                        // 3. 기존 Draft API 허용
                        .requestMatchers("/api/drafts/**").permitAll()

                        // 4. 나머지는 인증 필요
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    // [추가] 8081 앱을 위한 CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        // 사진(image_6f9380.png)의 도메인들을 모두 허용합니다.
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://13.209.214.254",
                "https://jhs-platform.co.kr",
                "https://customer.jhs-platform.co.kr",
                "https://admin.jhs-platform.co.kr",
                "https://manager.jhs-platform.co.kr",
                "https://counselor.jhs-platform.co.kr"
        ));

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}