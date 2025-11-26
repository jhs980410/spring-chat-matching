package com.chatmatchingservice.springchatmatching.global.auth.jwt;

import com.chatmatchingservice.springchatmatching.domain.counselor.entity.Counselor;
import com.chatmatchingservice.springchatmatching.domain.counselor.repository.CounselorRepository;
import com.chatmatchingservice.springchatmatching.domain.user.entity.AppUser;
import com.chatmatchingservice.springchatmatching.domain.user.repository.AppUserRepository;
import com.chatmatchingservice.springchatmatching.global.auth.ChatPrincipal;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService {

    private final AppUserRepository userRepository;
    private final CounselorRepository counselorRepository;

    public UserDetails loadUserByIdAndRole(Long id, String role) {

        if ("USER".equals(role)) {
            AppUser user = userRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

            return new JwtUserDetails(
                    user.getId(),
                    "USER",
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }

        if ("COUNSELOR".equals(role)) {
            Counselor c = counselorRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

            return new JwtUserDetails(
                    c.getId(),
                    "COUNSELOR",
                    c.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_COUNSELOR"))
            );
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}
