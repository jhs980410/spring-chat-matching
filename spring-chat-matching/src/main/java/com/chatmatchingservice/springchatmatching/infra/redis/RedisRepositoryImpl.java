package com.chatmatchingservice.springchatmatching.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;         // JSON 용 (WS 메시지, 객체 저장)
    private final RedisTemplate<String, String> redisStringTemplate;  // 문자열 용 (status, load, category 등)

    // ==========================================
    // 상담사 상태 / Load
    // ==========================================

    @Override
    public void setCounselorStatus(Long counselorId, String status) {
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.counselorStatus(counselorId),
                status
        );
    }

    @Override
    public String getCounselorStatus(Long counselorId) {
        return redisStringTemplate.opsForValue().get(
                RedisKeyManager.counselorStatus(counselorId)
        );
    }

    @Override
    public void setCounselorLoad(Long counselorId, long load) {
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.counselorLoad(counselorId),
                String.valueOf(load)
        );
    }

    @Override
    public long getCounselorLoad(Long counselorId) {
        String v = redisStringTemplate.opsForValue().get(
                RedisKeyManager.counselorLoad(counselorId)
        );
        if (v == null) return 0L;

        try {
            return Long.parseLong(v);
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public long incrementCounselorLoad(Long counselorId, long delta) {
        Long result = redisStringTemplate.opsForValue()
                .increment(RedisKeyManager.counselorLoad(counselorId), delta);
        return result != null ? result : 0L;
    }

    @Override
    public void setCounselorLastFinished(Long counselorId, long timestamp) {
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.counselorLastFinished(counselorId),
                String.valueOf(timestamp)
        );
    }


    // ==========================================
    // 상담사 → 카테고리 목록
    // ==========================================
    @Override

    public String wsChannel(Long sessionId) {

        return "ws:session:" + sessionId;

    }
    @Override
    public void setCounselorCategories(Long counselorId, List<Long> categoryIds) {
        String joined = categoryIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.counselorCategories(counselorId),
                joined
        );
    }

    @Override
    public List<Long> getCounselorCategories(Long counselorId) {
        String v = redisStringTemplate.opsForValue().get(
                RedisKeyManager.counselorCategories(counselorId)
        );
        if (v == null || v.isBlank()) return List.of();

        return Arrays.stream(v.split(","))
                .map(Long::parseLong)
                .toList();
    }

    @Override
    public void deleteCounselorCategories(Long counselorId) {
        redisStringTemplate.delete(
                RedisKeyManager.counselorCategories(counselorId)
        );
    }


    @Override
    public Long getCounselorLastFinished(Long counselorId) {
        String v = redisStringTemplate.opsForValue().get(
                RedisKeyManager.counselorLastFinished(counselorId)
        );
        if (v == null) return null;

        try {
            return Long.parseLong(v);
        } catch (Exception e) {
            return null;
        }
    }

    // ==========================================
    // 카테고리 → 상담사 SET
    // ==========================================

    @Override
    public void addCounselorToCategory(Long categoryId, Long counselorId) {
        redisStringTemplate.opsForSet()
                .add(RedisKeyManager.categoryCounselors(categoryId), counselorId.toString());
    }

    @Override
    public void removeCounselorFromCategory(Long categoryId, Long counselorId) {
        redisStringTemplate.opsForSet()
                .remove(RedisKeyManager.categoryCounselors(categoryId), counselorId.toString());
    }

    @Override
    public Set<String> getCounselorsOfCategory(Long categoryId) {
        return redisStringTemplate.opsForSet()
                .members(RedisKeyManager.categoryCounselors(categoryId));
    }

    // ==========================================
    // 카테고리 Queue
    // ==========================================

    @Override
    public void enqueueSession(Long categoryId, Long sessionId) {
        redisStringTemplate.opsForList()
                .rightPush(RedisKeyManager.categoryQueue(categoryId), sessionId.toString());
    }

    @Override
    public Long dequeueSession(Long categoryId) {
        String v = redisStringTemplate.opsForList()
                .leftPop(RedisKeyManager.categoryQueue(categoryId));
        if (v == null) return null;

        try {
            return Long.parseLong(v);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getQueueSnapshot(Long categoryId) {
        return redisStringTemplate.opsForList()
                .range(RedisKeyManager.categoryQueue(categoryId), 0, -1);
    }

    @Override
    public void removeFromQueue(Long categoryId, Long sessionId) {
        redisStringTemplate.opsForList().remove(
                RedisKeyManager.categoryQueue(categoryId),
                0,
                sessionId.toString()
        );
    }

    // ==========================================
    // 세션 정보
    // ==========================================

    @Override
    public void setSessionStatus(Long sessionId, String status) {
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.sessionStatus(sessionId),
                status
        );
    }

    @Override
    public String getSessionStatus(Long sessionId) {
        return redisStringTemplate.opsForValue().get(
                RedisKeyManager.sessionStatus(sessionId)
        );
    }

    @Override
    public void setSessionUser(Long sessionId, Long userId) {
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.sessionUser(sessionId),
                userId.toString()
        );
    }

    @Override
    public Long getSessionUser(Long sessionId) {
        String v = redisStringTemplate.opsForValue().get(
                RedisKeyManager.sessionUser(sessionId)
        );
        return v != null ? Long.parseLong(v) : null;
    }

    @Override
    public void setSessionCounselor(Long sessionId, Long counselorId) {
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.sessionCounselor(sessionId),
                counselorId.toString()
        );
    }

    @Override
    public Long getSessionCounselor(Long sessionId) {
        String v = redisStringTemplate.opsForValue().get(
                RedisKeyManager.sessionCounselor(sessionId)
        );
        return v != null ? Long.parseLong(v) : null;
    }

    @Override
    public void setSessionCategory(Long sessionId, Long categoryId) {
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.sessionCategory(sessionId),
                categoryId.toString()
        );
    }

    @Override
    public Long getSessionCategory(Long sessionId) {
        String v = redisStringTemplate.opsForValue().get(
                RedisKeyManager.sessionCategory(sessionId)
        );
        return v != null ? Long.parseLong(v) : null;
    }

    // ==========================================
    // 세션 키 삭제
    // ==========================================

    @Override
    public void deleteSessionKeys(Long sessionId) {
        redisStringTemplate.delete(RedisKeyManager.sessionStatus(sessionId));
        redisStringTemplate.delete(RedisKeyManager.sessionUser(sessionId));
        redisStringTemplate.delete(RedisKeyManager.sessionCounselor(sessionId));
        redisStringTemplate.delete(RedisKeyManager.sessionCategory(sessionId));
    }

    // ==========================================
    // Waiting 세션 조회
    // ==========================================

    @Override
    public Long findWaitingSessionByUser(Long userId) {

        Set<String> keys = redisStringTemplate.keys("session:*:user");
        if (keys == null) return null;

        String target = userId.toString();

        for (String key : keys) {
            String v = redisStringTemplate.opsForValue().get(key);
            if (!target.equals(v)) continue;

            Long sessionId = extractSessionId(key);
            if (sessionId == null) continue;

            if ("WAITING".equals(getSessionStatus(sessionId))) {
                return sessionId;
            }
        }

        return null;
    }

    private Long extractSessionId(String key) {
        try {
            return Long.parseLong(key.split(":")[1]);
        } catch (Exception e) {
            return null;
        }
    }

    //세션별 상태별 세션

    public long countByStatus(String status) {
        // 키 조회
        Set<String> keys = redisStringTemplate.keys("session:*:status");

        if (keys == null || keys.isEmpty()) {
            return 0;
        }

        // 값 조회도 redisStringTemplate으로
        return keys.stream()
                .filter(key -> status.equals(redisStringTemplate.opsForValue().get(key)))
                .count();
    }

    // ==========================================
    // WebSocket Pub/Sub
    // ==========================================

    // RedisRepositoryImpl.java
    @Override
    public void publishToWsChannel(Long sessionId, Object message) {
        // 🔥 RedisPublisher 대신 RedisTemplate을 직접 사용하여 단일 발행을 보장
        // (이 로직이 RedisTemplate.convertAndSend()를 단 한 번만 호출하도록 보장해야 함)
        redisTemplate.convertAndSend(
                RedisKeyManager.wsChannel(sessionId),
                message
        );
    }

    @Override
    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }

    @Override
    public void setUserDisconnectTime(Long userId, long timestamp) {
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.userDisconnectTime(userId),
                String.valueOf(timestamp)
        );
    }

    @Override
    public Long getUserDisconnectTime(Long userId) {
        String v = redisStringTemplate.opsForValue().get(
                RedisKeyManager.userDisconnectTime(userId)
        );
        return v != null ? Long.parseLong(v) : null;
    }

    @Override
    public Long getActiveSessionIdByUser(Long userId) {

        Set<String> keys = redisStringTemplate.keys("session:*:user");
        if (keys == null) return null;

        String target = userId.toString();

        for (String key : keys) {
            String v = redisStringTemplate.opsForValue().get(key);
            if (!target.equals(v)) continue;

            Long sessionId = extractSessionId(key);
            if (sessionId == null) continue;

            if ("ACTIVE".equals(getSessionStatus(sessionId))) {
                return sessionId;
            }
        }

        return null;
    }

    // 좌석 락
    public static String seatLock(Long eventId, Long seatId) {
        return "seat:lock:event:" + eventId + ":seat:" + seatId;
    }

    // 유저가 잡은 좌석 목록
    public static String userLockedSeats(Long userId, Long eventId) {
        return "user:" + userId + ":event:" + eventId + ":lockedSeats";
    }

    // 예매 상태
    public static String reservationStatus(Long eventId, Long userId) {
        return "reservation:event:" + eventId + ":user:" + userId;
    }
    // ================================
// 🎟️ 좌석 예매 (Seat Lock)
// ================================

    @Override
    public boolean tryLockSeat(Long eventId, Long seatId, Long userId, long ttlSeconds) {
        String key = RedisKeyManager.seatLock(eventId, seatId);

        Boolean success = redisStringTemplate.opsForValue().setIfAbsent(
                key,
                userId.toString(),
                ttlSeconds,
                TimeUnit.SECONDS
        );

        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unlockSeat(Long eventId, Long seatId) {
        redisStringTemplate.delete(
                RedisKeyManager.seatLock(eventId, seatId)
        );
    }

    @Override
    public void addUserLockedSeat(Long userId, Long eventId, Long seatId) {
        redisStringTemplate.opsForSet().add(
                RedisKeyManager.userLockedSeats(userId, eventId),
                seatId.toString()
        );
    }

    @Override
    public Set<Long> getUserLockedSeats(Long userId, Long eventId) {
        Set<String> values = redisStringTemplate.opsForSet()
                .members(RedisKeyManager.userLockedSeats(userId, eventId));

        if (values == null) return Set.of();

        return values.stream()
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    @Override
    public void removeUserLockedSeat(Long userId, Long eventId, Long seatId) {
        redisStringTemplate.opsForSet().remove(
                RedisKeyManager.userLockedSeats(userId, eventId),
                seatId.toString()
        );
    }

    @Override
    public void clearUserLockedSeats(Long userId, Long eventId) {
        redisStringTemplate.delete(
                RedisKeyManager.userLockedSeats(userId, eventId)
        );
    }
    @Override
    public void setReservationStatus(Long eventId, Long userId, String status) {
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.reservationStatus(eventId, userId),
                status
        );
    }

    @Override
    public String getReservationStatus(Long eventId, Long userId) {
        return redisStringTemplate.opsForValue().get(
                RedisKeyManager.reservationStatus(eventId, userId)
        );
    }

    @Override
    public void clearReservationStatus(Long eventId, Long userId) {
        redisStringTemplate.delete(
                RedisKeyManager.reservationStatus(eventId, userId)
        );
    }



}
