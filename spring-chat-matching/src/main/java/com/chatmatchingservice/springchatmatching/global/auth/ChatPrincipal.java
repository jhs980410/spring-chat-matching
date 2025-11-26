package com.chatmatchingservice.springchatmatching.global.auth;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
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

    @Override
    public String getName() {
        // Principal.getName() 에는 식별 가능한 문자열 반환
        return role + ":" + id;
    }
}
