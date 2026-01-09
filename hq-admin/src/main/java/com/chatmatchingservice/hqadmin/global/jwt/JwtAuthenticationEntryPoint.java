package com.chatmatchingservice.hqadmin.global.jwt;

import com.chatmatchingservice.hqadmin.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        ErrorCode error = ErrorCode.UNAUTHORIZED;

        response.setStatus(error.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        String body = String.format(
                "{\"code\":\"%s\",\"message\":\"%s\"}",
                error.getCode(),
                error.getMessage()
        );

        response.getWriter().write(body);
    }
}
