package com.chatmatchingservice.springchatmatching.global.auth;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

/**
 * WebSocket용 Principal
 * - userId 또는 counselorId + role 포함
 * - Spring Security UserDetails 구현
 */
@Getter
public class ChatPrincipal implements Principal, UserDetails {

    private final Long id;      // userId 또는 counselorId
    private final String role;  // "USER" / "COUNSELOR"

    public ChatPrincipal(Long id, String role) {

        if (id == null || id <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (role == null || role.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!role.equals("USER") && !role.equals("COUNSELOR")) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.id = id;
        this.role = role;
    }

    /* ===============================
     * Principal
     * =============================== */
    @Override
    public String getName() {
        // WebSocket / 로그 / 식별용
        return role + ":" + id;
    }

    /* ===============================
     * UserDetails (핵심)
     * =============================== */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security 표준 ROLE_ 접두사
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public String getPassword() {
        return null; // JWT 기반 → 비밀번호 사용 안 함
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
