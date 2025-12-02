package com.chatmatchingservice.springchatmatching.global.config;

import com.chatmatchingservice.springchatmatching.global.auth.jwt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 최종 SecurityFilterChain 설정:
     * - CSRF 비활성화 (Stateless API)
     * - 세션 관리: STATELESS (JWT 사용)
     * - CORS 설정 (corsConfigurationSource Bean 사용)
     * - 인증/인가 예외 처리 핸들러 설정
     * - 경로별 접근 권한 설정
     * - JWT 필터 등록
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. 기본 설정 (CORS, CSRF, Session)
                // cors()에 configurationSource()를 명시적으로 전달합니다.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 2. 인증 & 인가 예외 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)  // 401 Unauthorized
                        .accessDeniedHandler(accessDeniedHandler)            // 403 Forbidden
                )

                // 3. 허용 경로 설정
                .authorizeHttpRequests(auth -> auth
                        // Auth API는 모두 허용
                        .requestMatchers("/api/auth/**").permitAll()

                        // WebSocket 핸드셰이크 허용
                        .requestMatchers("/ws/**", "/ws/connect").permitAll()

                        // 정적 HTML 허용
                        .requestMatchers("/user.html", "/counselor.html", "ws-test.html", "custom.html").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()

                        // Swagger 허용
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/my-swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/webjars/**"
                        ).permitAll()

                        // ============================
                        // 🔥 상담사 전용 Dashboard API
                        // ============================
                        .requestMatchers("/api/dashboard/**").hasAnyRole("COUNSELOR", "ADMIN")

                        // ============================
                        // 🔥 통계 API는 관리자 or 상담사
                        // ============================
                        .requestMatchers("/api/stats/**").hasAnyRole("COUNSELOR", "ADMIN")

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )


                // 4. JWT 필터 등록
                // JWT 검증을 UsernamePasswordAuthenticationFilter 이전에 수행하도록 등록
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * 비밀번호 암호화에 사용할 PasswordEncoder Bean 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS (Cross-Origin Resource Sharing) 설정 Bean 등록
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        // 로컬 개발환경 모두 허용
        config.addAllowedOriginPattern("http://localhost:*");
        config.addAllowedOriginPattern("http://127.0.0.1:*");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}