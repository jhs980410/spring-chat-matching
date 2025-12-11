package com.chatmatchingservice.springchatmatching.global.config;

import com.chatmatchingservice.springchatmatching.global.auth.jwt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final CookieUtil cookieUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1) CORS + CSRF + Stateless 세션
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 2) 인증 예외 처리 핸들러
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // 3) 경로별 요청 허용
                .authorizeHttpRequests(auth -> auth
                        // 인증(로그인) API
                        .requestMatchers("/api/auth/**").permitAll()

                        // WebSocket 핸드셰이크 허용
                        .requestMatchers("/ws/**", "/ws/connect").permitAll()

                        // ★ STOMP SEND 메시지 허용 (가장 중요!!)
                        .requestMatchers("/pub/**").permitAll()

                        // 테스트 페이지, 정적 파일
                        .requestMatchers(
                                "/user.html",
                                "/counselor.html",
                                "/ws-test.html",
                                "/custom.html",
                                "/favicon.ico"
                        ).permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/my-swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/webjars/**"
                        ).permitAll()

                        // 상담사 전용
                        .requestMatchers("/api/dashboard/**").hasAnyRole("COUNSELOR", "ADMIN")

                        // 통계 API
                        .requestMatchers("/api/stats/**").hasAnyRole("COUNSELOR", "ADMIN")

                        // 나머지는 로그인 필요
                        .anyRequest().authenticated()
                )

                // 4) JWT 필터 등록
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, cookieUtil),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        // 개발 환경 전체 허용
        config.addAllowedOriginPattern("*");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
