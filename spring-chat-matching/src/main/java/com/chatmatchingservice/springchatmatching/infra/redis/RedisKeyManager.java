package com.chatmatchingservice.springchatmatching.infra.redis;

public class RedisKeyManager {

    // ===================== 상담사 =====================
    public static String counselorStatus(long id) {
        return "counselor:" + id + ":status";
    }

    public static String counselorLoad(long id) {
        return "counselor:" + id + ":load";
    }

    public static String counselorLastFinished(long id) {
        return "counselor:" + id + ":lastFinishedAt";
    }

    // ===================== 카테고리 → 상담사 SET =====================
    public static String categoryCounselors(long categoryId) {
        return "category:" + categoryId + ":counselors";
    }

    public static String categoryQueue(long categoryId) {
        return "queue:category:" + categoryId;
    }
    // 🔥 상담사 → 멀티 카테고리 목록 저장용 (Set)
    public static String counselorCategories(long counselorId) {
        return "counselor:" + counselorId + ":categories";
    }
    // ===================== 세션 =====================
    public static String sessionStatus(Long sessionId) {
        return "session:" + sessionId + ":status";
    }

    public static String sessionCounselor(Long sessionId) {
        return "session:" + sessionId + ":counselor";
    }

    public static String sessionUser(Long sessionId) {
        return "session:" + sessionId + ":user";
    }

    public static String sessionCategory(Long sessionId) {
        return "session:" + sessionId + ":category";
    }

    // ===================== WebSocket 채널 =====================
    public static String wsChannel(Long sessionId) {
        return "ws:session:" + sessionId;
    }
    public static String userDisconnectTime(Long userId) {
        return "user:" + userId + ":disconnectTime";
    }
}
