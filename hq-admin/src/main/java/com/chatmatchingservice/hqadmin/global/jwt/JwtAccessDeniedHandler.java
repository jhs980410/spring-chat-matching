package com.chatmatchingservice.hqadmin.global.jwt;

import com.chatmatchingservice.hqadmin.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        ErrorCode error = ErrorCode.FORBIDDEN;

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
