package com.chatmatchingservice.springchatmatching.global.auth.jwt;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        try {
            // Authorization 헤더가 있으면만 검사 (없으면 그냥 통과)
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                // 토큰 검증 (유효하지 않으면 CustomException 던짐)
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

            // 정상 흐름
            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            // 우리 쪽에서 던진 도메인/인증 예외
            SecurityContextHolder.clearContext();
            writeError(response, e.getErrorCode());

        } catch (Exception e) {
            // 그 외 알 수 없는 예외
            SecurityContextHolder.clearContext();
            writeError(response, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void writeError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        String body = String.format(
                "{\"code\":\"%s\",\"message\":\"%s\"}",
                errorCode.getCode(),
                errorCode.getMessage()
        );

        response.getWriter().write(body);
    }
}
