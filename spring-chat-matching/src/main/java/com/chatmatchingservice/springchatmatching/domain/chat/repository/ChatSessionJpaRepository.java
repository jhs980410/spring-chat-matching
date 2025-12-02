package com.chatmatchingservice.springchatmatching.domain.chat.repository;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatSessionJpaRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findTopByUserIdAndStatusIn(Long userId, List<SessionStatus> statuses);
    Optional<ChatSession> findTopByCounselorIdAndStatusIn(Long counselorId, List<SessionStatus> statuses);
    //오늘 상담 목록 조회 Raw (대시보드 표)
    @Query(value =
            "SELECT s.id AS sessionId, " +
                    "       u.nickname AS userName, " +
                    "       c.name AS categoryName, " +
                    "       s.started_at AS startedAt, " +
                    "       s.ended_at AS endedAt, " +
                    "       s.status AS status " +
                    "FROM chat_session s " +
                    "JOIN app_user u ON s.user_id = u.id " +
                    "JOIN category c ON s.category_id = c.id " +
                    "WHERE DATE(s.started_at) = CURDATE() " +
                    "ORDER BY s.started_at ASC",
            nativeQuery = true)
    List<Object[]> findTodaySessionsRaw();

    //상담사별 처리량(Bar Chart)

    @Query(value =
            "SELECT c.id AS counselorId, c.name AS counselorName, COUNT(s.id) AS handledCount " +
                    "FROM counselor c " +
                    "LEFT JOIN chat_session s ON c.id = s.counselor_id AND s.status = 'ENDED' " +
                    "GROUP BY c.id",
            nativeQuery = true)
    List<Object[]> getCounselorHandled();

    //KPI – 총 종료 상담 수
    @Query(value = "SELECT COUNT(*) FROM chat_session WHERE status = 'ENDED'", nativeQuery = true)
    long countTotalHandled();

    //KPI – 평균 상담 시간
    @Query(value = "SELECT AVG(duration_sec) FROM chat_session WHERE duration_sec > 0", nativeQuery = true)
    Double avgDuration();
    //KPI – 평균 만족도 (CounselLog에서 가져옴)
    @Query(value = "SELECT AVG(satisfaction_score) FROM counsel_log", nativeQuery = true)
    Double avgScore();
}
