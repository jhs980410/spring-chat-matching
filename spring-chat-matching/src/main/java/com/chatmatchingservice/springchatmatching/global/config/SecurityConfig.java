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
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/ws/connect").permitAll() // 필요하다면 ws/connect 경로도 명시적으로 허용

                        // ===== 정적 HTML 허용 (프론트엔드 개발용) =====
                        .requestMatchers("/user.html", "/counselor.html","ws-test.html","custom.html").permitAll()
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

        // 인증 정보 (쿠키 등) 교환 허용
        config.setAllowCredentials(true);

        // 허용할 출처 (프론트엔드 주소)
        // **주의: 배포 시 "http://your-domain.com"을 실제 도메인으로 변경해야 합니다.**
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://127.0.0.1:5173");
        // 모든 출처를 허용하려면 config.addAllowedOrigin("*"); 대신
        // config.setAllowedOriginPatterns(List.of("*"));를 사용해야 합니다.
        // 여기서는 명시된 로컬호스트만 허용합니다.

        // 모든 요청 헤더와 HTTP 메서드 허용
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // UrlBasedCorsConfigurationSource를 사용하여 모든 경로에 대해 위 설정을 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}