package com.chatmatchingservice.springchatmatching.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;


    // ==========================================
    // 상담사 상태 / Load
    // ==========================================
    @Override
    public void setCounselorStatus(Long counselorId, String status) {
        redisTemplate.opsForValue().set(RedisKeyManager.counselorStatus(counselorId), status);
    }

    @Override
    public String getCounselorStatus(Long counselorId) {
        Object value = redisTemplate.opsForValue().get(RedisKeyManager.counselorStatus(counselorId));
        return value != null ? value.toString() : null;
    }

    @Override
    public void setCounselorLoad(Long counselorId, long load) {
        redisTemplate.opsForValue().set(RedisKeyManager.counselorLoad(counselorId), String.valueOf(load));
    }

    @Override
    public long getCounselorLoad(Long counselorId) {
        Object val = redisTemplate.opsForValue().get(RedisKeyManager.counselorLoad(counselorId));
        if (val == null) return 0L;

        try {
            return Long.parseLong(val.toString());
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public long incrementCounselorLoad(Long counselorId, long delta) {
        Long result = redisTemplate.opsForValue()
                .increment(RedisKeyManager.counselorLoad(counselorId), delta);
        return result != null ? result : 0L;
    }

    @Override
    public void setCounselorLastFinished(Long counselorId, long timestamp) {
        redisTemplate.opsForValue().set(
                RedisKeyManager.counselorLastFinished(counselorId),
                String.valueOf(timestamp)
        );
    }
    // ==========================================
// 상담사 → 카테고리 목록 (멀티 READY지원)
// ==========================================
    @Override
    public void setCounselorCategories(Long counselorId, List<Long> categoryIds) {
        String key = RedisKeyManager.counselorCategories(counselorId);

        String joined = categoryIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        redisTemplate.opsForValue().set(key, joined);
    }

    @Override
    public List<Long> getCounselorCategories(Long counselorId) {
        String key = RedisKeyManager.counselorCategories(counselorId);

        String value = (String) redisTemplate.opsForValue().get(key);
        if (value == null || value.isBlank()) return List.of();

        return Arrays.stream(value.split(","))
                .map(Long::parseLong)
                .toList();
    }


    @Override
    public void deleteCounselorCategories(Long counselorId) {
        redisTemplate.delete(RedisKeyManager.counselorCategories(counselorId));
    }
    @Override
    public Long getCounselorLastFinished(Long counselorId) {
        Object val = redisTemplate.opsForValue().get(RedisKeyManager.counselorLastFinished(counselorId));

        if (val == null) return null;

        try {
            return Long.parseLong(val.toString());
        } catch (Exception e) {
            return null;
        }
    }


    // ==========================================
    // 카테고리 → 상담사 SET
    // ==========================================
    @Override
    public void addCounselorToCategory(Long categoryId, Long counselorId) {
        redisTemplate.opsForSet()
                .add(RedisKeyManager.categoryCounselors(categoryId), counselorId.toString());
    }

    @Override
    public void removeCounselorFromCategory(Long categoryId, Long counselorId) {
        redisTemplate.opsForSet()
                .remove(RedisKeyManager.categoryCounselors(categoryId), counselorId.toString());
    }

    @Override
    public Set<Object> getCounselorsOfCategory(Long categoryId) {
        return redisTemplate.opsForSet()
                .members(RedisKeyManager.categoryCounselors(categoryId));
    }


    // ==========================================
    // 카테고리 Queue (대기열)
    // ==========================================
    @Override
    public void enqueueSession(Long categoryId, Long sessionId) {
        redisTemplate.opsForList()
                .rightPush(RedisKeyManager.categoryQueue(categoryId), sessionId.toString());
    }

    @Override
    public Long dequeueSession(Long categoryId) {
        Object v = redisTemplate.opsForList()
                .leftPop(RedisKeyManager.categoryQueue(categoryId));

        if (v == null) return null;

        try {
            return Long.parseLong(v.toString());
        } catch (Exception e) {
            log.error("[RedisRepo] dequeue 변환 실패: {}", v);
            return null;
        }
    }

    @Override
    public List<Object> getQueueSnapshot(Long categoryId) {
        return redisTemplate.opsForList()
                .range(RedisKeyManager.categoryQueue(categoryId), 0, -1);
    }

    @Override
    public void removeFromQueue(Long categoryId, Long sessionId) {
        try {
            redisTemplate.opsForList().remove(
                    RedisKeyManager.categoryQueue(categoryId),
                    0,
                    sessionId.toString()
            );
        } catch (Exception e) {
            log.error("[RedisRepo] removeFromQueue 실패: categoryId={}, sessionId={}", categoryId, sessionId, e);
        }
    }


    // ==========================================
    // 세션 정보
    // ==========================================
    @Override
    public void setSessionStatus(Long sessionId, String status) {
        redisTemplate.opsForValue().set(
                RedisKeyManager.sessionStatus(sessionId),
                status
        );
    }

    @Override
    public String getSessionStatus(Long sessionId) {
        Object v = redisTemplate.opsForValue().get(RedisKeyManager.sessionStatus(sessionId));
        return v != null ? v.toString() : null;
    }


    @Override
    public void setSessionUser(Long sessionId, Long userId) {
        redisTemplate.opsForValue().set(
                RedisKeyManager.sessionUser(sessionId),
                userId.toString()
        );
    }

    @Override
    public Long getSessionUser(Long sessionId) {
        Object v = redisTemplate.opsForValue().get(RedisKeyManager.sessionUser(sessionId));
        return v != null ? Long.parseLong(v.toString()) : null;
    }


    @Override
    public void setSessionCounselor(Long sessionId, Long counselorId) {
        redisTemplate.opsForValue().set(
                RedisKeyManager.sessionCounselor(sessionId),
                counselorId.toString()
        );
    }

    @Override
    public Long getSessionCounselor(Long sessionId) {
        Object v = redisTemplate.opsForValue().get(RedisKeyManager.sessionCounselor(sessionId));
        return v != null ? Long.parseLong(v.toString()) : null;
    }


    @Override
    public void setSessionCategory(Long sessionId, Long categoryId) {
        redisTemplate.opsForValue().set(
                RedisKeyManager.sessionCategory(sessionId),
                categoryId.toString()
        );
    }

    @Override
    public Long getSessionCategory(Long sessionId) {
        Object v = redisTemplate.opsForValue().get(RedisKeyManager.sessionCategory(sessionId));
        return v != null ? Long.parseLong(v.toString()) : null;
    }


    // ==========================================
    // 세션 키 삭제
    // ==========================================
    @Override
    public void deleteSessionKeys(Long sessionId) {
        redisTemplate.delete(RedisKeyManager.sessionStatus(sessionId));
        redisTemplate.delete(RedisKeyManager.sessionUser(sessionId));
        redisTemplate.delete(RedisKeyManager.sessionCounselor(sessionId));
        redisTemplate.delete(RedisKeyManager.sessionCategory(sessionId));
    }


    // ==========================================
    // WAITING 세션 조회 (userId 기준)
    // ==========================================
    @Override
    public Long findWaitingSessionByUser(Long userId) {

        Set<String> keys = redisTemplate.keys("session:*:user");
        if (keys == null || keys.isEmpty()) return null;

        String target = userId.toString();

        for (String key : keys) {

            Object v = redisTemplate.opsForValue().get(key);
            if (v == null) continue;

            if (!target.equals(v.toString())) continue;

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
            log.warn("[RedisRepo] sessionId 파싱 실패 key={}", key);
            return null;
        }
    }


    // ==========================================
    // WebSocket Pub/Sub
    // ==========================================
    @Override
    public String wsChannel(Long sessionId) {
        return "ws:session:" + sessionId;
    }

    @Override
    public void publishToWsChannel(Long sessionId, Object message) {
        redisTemplate.convertAndSend(
                RedisKeyManager.wsChannel(sessionId),
                message
        );
    }
}
