package com.chatmatchingservice.hqadmin.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CookieUtil cookieUtil) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieUtil = cookieUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = null;

        // 1️⃣ Authorization 헤더 (옵션)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            log.debug("JWT found in Authorization header.");
        }

        // 2️⃣ HttpOnly 쿠키 (주 방식)
        if (token == null) {
            token = cookieUtil.getAccessToken(request);
            if (token != null) {
                log.debug("JWT found in HttpOnly Cookie.");
            }
        }

        // 3️⃣ 인증 시도 (성공 시에만 Context 세팅)
        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("Authentication success");
                }
            } catch (Exception e) {
                // ❗ 여기서 절대 response 쓰지 말 것
                SecurityContextHolder.clearContext();
                log.debug("JWT invalid or expired, continue without authentication");
            }
        }

        // 4️⃣ 무조건 다음 필터로
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.equals("/");
    }
}
