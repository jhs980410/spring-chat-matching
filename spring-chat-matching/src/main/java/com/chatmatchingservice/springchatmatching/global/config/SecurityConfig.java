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
import org.springframework.http.HttpMethod;
import java.util.List;

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
                        // 로그인, 회원가입은 누구나 가능
                        .requestMatchers("/api/auth/login", "/api/auth/user/signup","/api/auth/user/login","/api/auth/counselor/login", "/api/auth/refresh").permitAll()

                        // 내 정보 조회(/api/auth/me)는 인증된 사용자만 가능하도록 제외하거나 상단에 명시
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers("/api/auth/me/orders").authenticated()
                        //이벤트 공개
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
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
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
