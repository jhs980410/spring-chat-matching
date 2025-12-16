package com.chatmatchingservice.springchatmatching.infra.redis;

import java.util.List;
import java.util.Set;

public interface RedisRepository {

    // ================================
    // 상담사 상태 / Load
    // ================================
    void setCounselorStatus(Long counselorId, String status);
    String getCounselorStatus(Long counselorId);

    void setCounselorLoad(Long counselorId, long load);
    long getCounselorLoad(Long counselorId);

    long incrementCounselorLoad(Long counselorId, long delta);

    void setCounselorLastFinished(Long counselorId, long timestamp);
    Long getCounselorLastFinished(Long counselorId);


    // ================================
    // 카테고리 → 상담사 SET
    // ================================
    void addCounselorToCategory(Long categoryId, Long counselorId);
    void removeCounselorFromCategory(Long categoryId, Long counselorId);
    Set<String> getCounselorsOfCategory(Long categoryId);

    Long findWaitingSessionByUser(Long userId);


    // ================================
    // 카테고리 Queue (대기열)
    // ================================
    void enqueueSession(Long categoryId, Long sessionId);
    Long dequeueSession(Long categoryId);
    List<String> getQueueSnapshot(Long categoryId); // 디버깅용

    // 🔥 추가: 특정 세션을 대기열에서 제거
    void removeFromQueue(Long categoryId, Long sessionId);
    // 멀티 카테고리 상담사 전용
    void setCounselorCategories(Long counselorId, List<Long> categoryIds);
    List<Long> getCounselorCategories(Long counselorId);
    void deleteCounselorCategories(Long counselorId);

    // ================================
    // 세션 정보
    // ================================
    void setSessionStatus(Long sessionId, String status);
    String getSessionStatus(Long sessionId);

    void setSessionUser(Long sessionId, Long userId);
    Long getSessionUser(Long sessionId);

    void setSessionCounselor(Long sessionId, Long counselorId);
    Long getSessionCounselor(Long sessionId);

    void setSessionCategory(Long sessionId, Long categoryId);
    Long getSessionCategory(Long sessionId);

    // 🔥 추가: 세션 관련 Redis 키 전체 삭제
    void deleteSessionKeys(Long sessionId);


    // ================================
    // WebSocket Channel
    // ================================
    String wsChannel(Long sessionId);
    void publishToWsChannel(Long sessionId, Object message);

    void publish(String channel, Object message);
    // 🔥 유저 disconnect 시간 기록
    void setUserDisconnectTime(Long userId, long timestamp);
    Long getUserDisconnectTime(Long userId);

    // 🔥 유저의 현재 ACTIVE 세션 ID 조회
    Long getActiveSessionIdByUser(Long userId);


   //"현재 상태별 세션 수".
    long countByStatus(String status);
}
