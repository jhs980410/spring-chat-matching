package infra.redis;


public class RedisKeyManager {

    // 상담사 상태
    public static String counselorStatus(long counselorId) {
        return "counselor:" + counselorId + ":status";
    }

    public static String counselorLoad(long counselorId) {
        return "counselor:" + counselorId + ":load";
    }

    public static String counselorLastFinished(long counselorId) {
        return "counselor:" + counselorId + ":lastFinishedAt";
    }

    // 카테고리 기반 상담사 SET
    public static String categoryCounselors(long categoryId) {
        return "category:" + categoryId + ":counselors";
    }

    // 대기열
    public static String categoryQueue(long categoryId) {
        return "queue:category:" + categoryId;
    }

    // 세션 상태
    public static String sessionStatus(String sessionId) {
        return "session:" + sessionId + ":status";
    }

    public static String sessionCounselor(String sessionId) {
        return "session:" + sessionId + ":counselor";
    }

    // WebSocket Pub/Sub 채널
    public static String wsChannel(String sessionId) {
        return "ws:session:" + sessionId;
    }
}