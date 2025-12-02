package com.chatmatchingservice.springchatmatching.global.auth.jwt;

import com.chatmatchingservice.springchatmatching.auth.dto.AuthResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private static final String ACCESS_TOKEN_NAME = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN_NAME = "REFRESH_TOKEN";

    // Access Token 유효 기간: 1시간 (60 * 60)
    private static final long ACCESS_MAX_AGE = 60 * 60;

    // Refresh Token 유효 기간: 14일 (60 * 60 * 24 * 14)
    private static final long REFRESH_MAX_AGE = 60L * 60 * 24 * 14;

    /**
     * Access Token과 Refresh Token을 HttpOnly 쿠키로 생성하여 응답 헤더에 추가합니다.
     * @param response HTTP 응답 객체
     * @param authResponse AuthService에서 반환된 토큰 정보를 담은 DTO
     */
    public void addTokenCookiesToResponse(HttpServletResponse response, AuthResponse authResponse) {
        // 1. Access Token 쿠키 생성
        ResponseCookie accessCookie = createCookie(
                ACCESS_TOKEN_NAME,
                authResponse.accessToken(),
                ACCESS_MAX_AGE
        );

        // 2. Refresh Token 쿠키 생성
        ResponseCookie refreshCookie = createCookie(
                REFRESH_TOKEN_NAME,
                authResponse.refreshToken(),
                REFRESH_MAX_AGE
        );

        // 3. 응답 헤더에 추가
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    /**
     * 공통 쿠키 설정을 적용하여 ResponseCookie를 생성합니다.
     */
    private ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)       // JavaScript 접근 불가 (HttpOnly)
                .secure(false)         // HTTPS 환경에서만 전송 (Secure) 로컬에서는 false
                .sameSite("Lax")     // SameSite=None + Secure(true) 조합은 HTTPS 필수
                .path("/")            // 모든 경로에서 유효
                .maxAge(maxAge)       // 유효 기간 설정
                .build();
    }
}