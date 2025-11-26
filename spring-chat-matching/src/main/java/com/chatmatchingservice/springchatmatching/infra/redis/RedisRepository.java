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
    Set<Object> getCounselorsOfCategory(Long categoryId);

    Long findWaitingSessionByUser(Long userId);


    // ================================
    // 카테고리 Queue (대기열)
    // ================================
    void enqueueSession(Long categoryId, Long sessionId);
    Long dequeueSession(Long categoryId);
    List<Object> getQueueSnapshot(Long categoryId); // 디버깅용

    // 🔥 추가: 특정 세션을 대기열에서 제거
    void removeFromQueue(Long categoryId, Long sessionId);


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
}
