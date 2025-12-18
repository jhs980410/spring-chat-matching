package com.chatmatchingservice.springchatmatching.auth.service;

import com.chatmatchingservice.springchatmatching.auth.dto.*;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorLoginRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorSignupRequest;
import com.chatmatchingservice.springchatmatching.domain.user.dto.UserLoginRequest;
import com.chatmatchingservice.springchatmatching.domain.user.dto.UserSignupRequest;
import com.chatmatchingservice.springchatmatching.domain.user.entity.AppUser;
import com.chatmatchingservice.springchatmatching.domain.user.entity.UserStatus;
import com.chatmatchingservice.springchatmatching.domain.user.repository.AppUserRepository;
import com.chatmatchingservice.springchatmatching.domain.counselor.entity.Counselor;
import com.chatmatchingservice.springchatmatching.domain.counselor.repository.CounselorRepository;
import com.chatmatchingservice.springchatmatching.global.auth.jwt.CookieUtil;
import com.chatmatchingservice.springchatmatching.global.auth.jwt.JwtTokenProvider;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final CounselorRepository counselorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    // ============================================
    // USER SIGNUP
    // ============================================
    public void userSignup(UserSignupRequest req) {

        if (userRepository.existsByEmail(req.email()))
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);

        AppUser user = AppUser.builder()
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .nickname(req.nickname())
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
    }

    // ============================================
    // USER LOGIN
    // ============================================
    public AuthResponse userLogin(UserLoginRequest req) {

        AppUser user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(req.password(), user.getPassword()))
            throw new CustomException(ErrorCode.LOGIN_FAILED);

        String access = jwtTokenProvider.generateAccessToken(user.getId(), "USER");
        String refresh = jwtTokenProvider.generateRefreshToken(user.getId());

        return new AuthResponse(access, refresh, user.getId(), "USER");
    }

    // ============================================
    // COUNSELOR SIGNUP
    // ============================================
    public void counselorSignup(CounselorSignupRequest req) {

        if (counselorRepository.existsByEmail(req.email()))
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);

        Counselor counselor = Counselor.builder()
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .name(req.name())
                .build();

        counselorRepository.save(counselor);
    }

    // ============================================
    // COUNSELOR LOGIN
    // ============================================
    public AuthResponse counselorLogin(CounselorLoginRequest req) {

        Counselor c = counselorRepository.findByEmail(req.email())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(req.password(), c.getPassword()))
            throw new CustomException(ErrorCode.LOGIN_FAILED);

        String access = jwtTokenProvider.generateAccessToken(c.getId(), "COUNSELOR");
        String refresh = jwtTokenProvider.generateRefreshToken(c.getId());

        return new AuthResponse(access, refresh, c.getId(), "COUNSELOR");
    }

    public AuthResponse refresh(HttpServletRequest request) {

        String refreshToken = cookieUtil.getRefreshToken(request);

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String role = jwtTokenProvider.getRole(refreshToken);

        String newAccess = jwtTokenProvider.generateAccessToken(userId, role);

        return new AuthResponse(newAccess, refreshToken, userId, role);
    }
    // ============================================
// GET CURRENT USER INFO (새로고침 시 정보 복구용)
// ============================================
    public AuthResponse getCurrentUserInfo(HttpServletRequest request) {
        // 1. 쿠키에서 Access Token 추출
        String accessToken = cookieUtil.getAccessToken(request);

        // 2. 토큰 유효성 검사
        if (accessToken == null || !jwtTokenProvider.validateToken(accessToken)) {
            // 토큰이 없거나 만료되었다면 리프레시 토큰 시도 혹은 에러 처리
            // 여기서는 유효하지 않으면 예외를 던져 프론트에서 로그아웃 처리하게 함
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // 3. 토큰에서 유저 정보 추출 (ID, Role)
        Long id = jwtTokenProvider.getUserId(accessToken);
        String role = jwtTokenProvider.getRole(accessToken);

        // Refresh Token은 굳이 새로 발급하지 않고 기존 것을 유지하거나
        // 로직에 따라 다시 가져올 수 있습니다.
        String refreshToken = cookieUtil.getRefreshToken(request);

        return new AuthResponse(accessToken, refreshToken, id, role);
    }
}
