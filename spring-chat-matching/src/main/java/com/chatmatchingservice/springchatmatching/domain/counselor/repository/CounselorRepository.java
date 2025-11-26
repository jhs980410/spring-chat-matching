package com.chatmatchingservice.springchatmatching.domain.counselor.repository;

import com.chatmatchingservice.springchatmatching.domain.counselor.entity.Counselor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounselorRepository extends JpaRepository<Counselor, Long> {
    Optional<Counselor> findByEmail(String email);
    boolean existsByEmail(String email);
}