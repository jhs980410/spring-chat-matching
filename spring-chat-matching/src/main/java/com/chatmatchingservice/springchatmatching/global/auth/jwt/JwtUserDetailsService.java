package com.chatmatchingservice.springchatmatching.global.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            Long id = Long.valueOf(userId);
            return new JwtUserDetails(id, "USER");   // 필요 시 DB에서 role 조회 가능
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found: " + userId);
        }
    }
}
