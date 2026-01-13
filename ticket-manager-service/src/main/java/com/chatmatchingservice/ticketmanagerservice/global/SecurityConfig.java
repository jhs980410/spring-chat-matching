package com.chatmatchingservice.ticketmanagerservice.global;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // POST 요청을 위해 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        // 1. Swagger 및 API 문서 관련 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 2. 매니저 판매 계약 및 공연/티켓 Draft API 허용 (추가된 부분)
                        .requestMatchers("/api/manager/**").permitAll()

                        // 3. 기존 Draft API 허용
                        .requestMatchers("/api/drafts/**").permitAll()

                        // 4. 나머지는 인증 필요 (또는 상황에 따라 denyAll 유지)
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}