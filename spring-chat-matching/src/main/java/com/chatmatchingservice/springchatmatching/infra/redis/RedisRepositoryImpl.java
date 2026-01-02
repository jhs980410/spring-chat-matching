package com.chatmatchingservice.springchatmatching.infra.redis;

import com.chatmatchingservice.springchatmatching.domain.mypage.dto.HomeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
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

// ================================
// 🎟️ 좌석 예매 (Seat Lock) - ORDER 기준
// ================================

    @Override
    public boolean tryLockSeat(
            Long eventId,
            Long seatId,
            Long orderId,
            long ttlSeconds
    ) {
        String key = RedisKeyManager.seatLock(eventId, seatId);

        Boolean success = redisStringTemplate.opsForValue().setIfAbsent(
                key,
                orderId.toString(),
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
    public void addOrderLockedSeat(Long orderId, Long eventId, Long seatId) {
        redisStringTemplate.opsForSet().add(
                RedisKeyManager.orderLockedSeats(orderId, eventId),
                seatId.toString()
        );
    }

    @Override
    public Set<Long> getOrderLockedSeats(Long orderId, Long eventId) {
        Set<String> values = redisStringTemplate.opsForSet()
                .members(RedisKeyManager.orderLockedSeats(orderId, eventId));

        if (values == null) return Set.of();

        return values.stream()
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    @Override
    public void clearOrderLockedSeats(Long orderId, Long eventId) {
        redisStringTemplate.delete(
                RedisKeyManager.orderLockedSeats(orderId, eventId)
        );
    }

    @Override
    public void setReservationStatus(
            Long eventId,
            Long orderId,
            String status
    ) {
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.reservationStatus(eventId, orderId),
                status
        );
    }

    @Override
    public String getReservationStatus(Long eventId, Long orderId) {
        return redisStringTemplate.opsForValue().get(
                RedisKeyManager.reservationStatus(eventId, orderId)
        );
    }

    @Override
    public void clearReservationStatus(Long eventId, Long orderId) {
        redisStringTemplate.delete(
                RedisKeyManager.reservationStatus(eventId, orderId)
        );
    }

    @Override
    public boolean isSeatLocked(Long eventId, Long seatId) {
        return Boolean.TRUE.equals(
                redisStringTemplate.hasKey(
                        RedisKeyManager.seatLock(eventId, seatId)
                )
        );
    }

    @Override
    public void setHomeCache(HomeResponseDto data, long ttlMinutes) {
        // 객체 저장 시 만료 시간(TTL)을 설정하여 메모리 효율을 관리합니다.
        redisTemplate.opsForValue().set(
                RedisKeyManager.homeCache(),
                data,
                Duration.ofMinutes(ttlMinutes)
        );
    }

    @Override
    public HomeResponseDto getHomeCache() {
        // RedisTemplate이 자동으로 JSON을 객체로 역직렬화합니다.
        Object data = redisTemplate.opsForValue().get(RedisKeyManager.homeCache());
        return (HomeResponseDto) data;
    }

    @Override
    public void evictHomeCache() {
        redisTemplate.delete(RedisKeyManager.homeCache());
    }

    @Override
    public void addToWaitingQueue(Long eventId, Long userId, long score) {
        // redisStringTemplate 사용
        redisStringTemplate.opsForZSet().add(
                RedisKeyManager.waitingQueue(eventId),
                userId.toString(),
                score
        );
    }

    @Override
    public Long getWaitingRank(Long eventId, Long userId) {
        // redisStringTemplate 사용
        return redisStringTemplate.opsForZSet().rank(
                RedisKeyManager.waitingQueue(eventId),
                userId.toString()
        );
    }

    @Override
    public Set<String> popWaitingUsers(Long eventId, int count) {
        String key = RedisKeyManager.waitingQueue(eventId);
        // redisStringTemplate은 바로 Set<String>을 반환하므로 casting 에러가 없습니다.
        Set<String> users = redisStringTemplate.opsForZSet().range(key, 0, count - 1);

        if (users == null || users.isEmpty()) return Collections.emptySet();

        // 대기열에서 삭제
        redisStringTemplate.opsForZSet().remove(key, users.toArray());
        return users;
    }

    @Override
    public void setAccessPass(Long eventId, Long userId, long ttlMinutes) {
        // redisStringTemplate 사용 (단순 문자열 저장)
        redisStringTemplate.opsForValue().set(
                RedisKeyManager.accessPass(eventId, userId),
                "VALID",
                Duration.ofMinutes(ttlMinutes)
        );
    }

    @Override
    public boolean hasAccessPass(Long eventId, Long userId) {
        String key = RedisKeyManager.accessPass(eventId, userId);
        return Boolean.TRUE.equals(redisStringTemplate.hasKey(key));
    }

}
