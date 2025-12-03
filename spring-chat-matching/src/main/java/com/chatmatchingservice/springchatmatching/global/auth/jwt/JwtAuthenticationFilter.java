package com.chatmatchingservice.springchatmatching.global.auth.jwt;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j // 로깅 추가
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil; // ⬅️ CookieUtil 추가

    // ⬅️ 생성자 수정 (CookieUtil 주입)
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

        // 1. Authorization 헤더에서 토큰 추출 시도 (기존 로직 유지 가능)
        String token = null;
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            log.debug("JWT found in Authorization header.");
        }

        // 2. 헤더에 토큰이 없으면, HttpOnly 쿠키에서 Access Token 추출 시도
        if (token == null) {
            token = cookieUtil.getAccessToken(request); // ⬅ 쿠키 추출
            if (token != null) {
                log.debug("JWT found in HttpOnly Cookie.");
            }
        }

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Authentication successful for token: {}", token);
            } else if (token != null) {
                log.warn("Token validation failed or token is null (Token: {})", token);
            }

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            log.error("Authentication failed with CustomException: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            writeError(response, e.getErrorCode());

        } catch (Exception e) {
            log.error("Authentication failed with unexpected exception.", e);
            SecurityContextHolder.clearContext();
            writeError(response, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void writeError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        if (response.isCommitted()) {
            log.warn("Response already committed, cannot write error.");
            return;
        }

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(
                "{\"code\":\"" + errorCode.getCode() + "\",\"message\":\"" + errorCode.getMessage() + "\"}"
        );
    }

    // shouldNotFilter 로직은 변경 없음
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