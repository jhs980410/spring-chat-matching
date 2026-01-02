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



// ===================== 🎟️ 좌석 예매 (ORDER 기준) =====================

    /** 좌석 락 (value = orderId, TTL 필수) */
    public static String seatLock(Long eventId, Long seatId) {
        return "seat:lock:event:" + eventId + ":seat:" + seatId;
    }

    /** 주문이 점유한 좌석 목록 */
    public static String orderLockedSeats(Long orderId, Long eventId) {
        return "order:" + orderId + ":event:" + eventId + ":seats";
    }

    /** 주문 단위 예매 상태 */
    public static String reservationStatus(Long eventId, Long orderId) {
        return "reservation:event:" + eventId + ":order:" + orderId;
    }



// ===================== 🚀 조회 성능 최적화 (Cache) =====================

    /** 홈 화면 응답 데이터 전체 캐싱용 키 */
    public static String homeCache() {
        return "cache:home:data";
    }

    /** 예매 전 대기열  */
    public static String waitingQueue(Long eventId) {
        return "event:waiting:" + eventId;
    }

    public static String accessPass(Long eventId, Long userId) {
        return "event:access:" + eventId + ":" + userId;
    }

}


