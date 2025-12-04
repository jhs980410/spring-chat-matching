package com.chatmatchingservice.springchatmatching.domain.log.repository;

import com.chatmatchingservice.springchatmatching.domain.log.entity.CounselLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounselLogRepository extends JpaRepository<CounselLog, Long> {

    Optional<CounselLog> findBySessionId(Long sessionId);

}
