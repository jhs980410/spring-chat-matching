package com.chatmatchingservice.springchatmatching.global.auth.jwt;

import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String userId) {

        // ★ 1) userId가 비어 있거나 잘못된 경우 → INVALID_INPUT_VALUE
        if (userId == null || userId.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            // ★ 2) 숫자 변환 실패 → INVALID_INPUT_VALUE
            Long id = Long.valueOf(userId);

            // ★ 3) 여기서 DB 조회가 필요하면 추가 가능
            // ex) User user = userRepository.findById(...)
            //     if (user == null) throw new CustomException(ErrorCode.UNAUTHORIZED);

            // 일단 기본 ROLE_USER
            return new JwtUserDetails(id, "USER");

        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);

        } catch (Exception e) {
            // 기타 알 수 없는 인증 오류 → UNAUTHORIZED
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}
