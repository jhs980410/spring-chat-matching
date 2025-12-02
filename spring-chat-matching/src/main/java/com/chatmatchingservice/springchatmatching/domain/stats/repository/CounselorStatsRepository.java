package com.chatmatchingservice.springchatmatching.domain.stats.repository;

import com.chatmatchingservice.springchatmatching.domain.stats.entity.CounselorStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CounselorStatsRepository extends JpaRepository<CounselorStats, Long> {

    @Query("SELECT cs FROM CounselorStats cs ORDER BY cs.statDate ASC")
    List<CounselorStats> findAllStats();

    @Query("SELECT cs FROM CounselorStats cs WHERE cs.counselorId = :counselorId ORDER BY cs.statDate ASC")
    List<CounselorStats> findByCounselorId(Long counselorId);
}
