package com.chatmatchingservice.hqadmin.global.jwt;


import com.chatmatchingservice.hqadmin.global.error.CustomException;
import com.chatmatchingservice.hqadmin.global.error.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtTokenProvider {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private final long ACCESS_EXP = 1000L * 60 * 30;
    private final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 14;


    // ============================================
    // 1. RSA 키 로드
    // ============================================
    @PostConstruct
    public void init() {
        try {
            ClassPathResource privatePem = new ClassPathResource("keys/private.pem");
            ClassPathResource publicPem = new ClassPathResource("keys/public.pem");

            String privateKeyContent = new String(privatePem.getInputStream().readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            String publicKeyContent = new String(publicPem.getInputStream().readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            privateKey = keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent))
            );
            publicKey = keyFactory.generatePublic(
                    new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent))
            );

            log.info("🔑 RSA 키 로딩 성공");

        } catch (Exception e) {
            log.error("❌ RSA 키 로딩 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    // ============================================
    // 2. ACCESS TOKEN 생성
    // ============================================
    public String generateAccessToken(Long userId, String role) {

        if (userId == null || role == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date(now))
                .expiration(new Date(now + ACCESS_EXP))
                .signWith(privateKey)
                .compact();
    }


    // ============================================
    // 3. REFRESH TOKEN 생성
    // ============================================
    public String generateRefreshToken(Long userId) {

        if (userId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date(now))
                .expiration(new Date(now + REFRESH_EXP))
                .signWith(privateKey)
                .compact();
    }


    // ============================================
    // 4. JWT 검증
    // ============================================
    public boolean validateToken(String token) {

        if (token == null || token.isBlank()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }


    // ============================================
    // 5. Authentication 생성
    // ============================================
    public Authentication getAuthentication(String token) {

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String role = claims.get("role", String.class);
            String subject = claims.getSubject();

            if (role == null || subject == null) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            Long userId = Long.valueOf(subject);

            return new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

        } catch (CustomException e) {
            throw e; // 우리가 던진 CustomException 그대로 유지

        } catch (Exception e) {
            log.warn("JWT 인증 정보 파싱 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }

    public String getRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

}
