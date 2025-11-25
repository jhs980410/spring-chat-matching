package com.chatmatchingservice.springchatmatching.global.auth;

import lombok.Getter;

import java.security.Principal;

/**
 * WebSocket용 Principal
 * - userId 또는 counselorId + role을 포함
 */
@Getter
public class ChatPrincipal implements Principal {

    private final Long id;      // userId 또는 counselorId
    private final String role;  // "USER" / "COUNSELOR"

    public ChatPrincipal(Long id, String role) {
        this.id = id;
        this.role = role;
    }

    @Override
    public String getName() {
        // Principal.getName() 에는 식별 가능한 문자열 리턴
        return role + ":" + id;
    }
}
