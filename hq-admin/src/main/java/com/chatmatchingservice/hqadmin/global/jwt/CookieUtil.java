package com.chatmatchingservice.hqadmin.global.jwt;

import com.chatmatchingservice.hqadmin.domain.auth.dto.AuthResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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

    /**
     * HttpServletRequest에서 ACCESS_TOKEN을 추출하여 반환합니다.
     * JwtAuthenticationFilter에서 HttpOnly 쿠키를 읽기 위해 사용됩니다.
     * @param request HttpServletRequest
     * @return Access Token 문자열 또는 null
     */
    public String getAccessToken(HttpServletRequest request) {
        return getTokenFromCookie(request, ACCESS_TOKEN_NAME);
    }

    public String getRefreshToken(HttpServletRequest request) {
        return getTokenFromCookie(request, REFRESH_TOKEN_NAME);
    }

    /**
     * 지정된 이름의 쿠키에서 값을 추출합니다.
     */
    private String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        // 쿠키 배열을 스트림으로 변환하여 원하는 이름의 쿠키를 찾고 값을 반환
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
    //Access만 다시 셋팅하는 함수
    public void updateAccessToken(HttpServletResponse response, String newAccess) {
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", newAccess)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
    }
    public void clearAuthCookies(HttpServletResponse response) {

        ResponseCookie clearAccess = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)  // 즉시 삭제
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", clearAccess.toString());
        response.addHeader("Set-Cookie", clearRefresh.toString());
    }

}