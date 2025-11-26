package com.chatmatchingservice.springchatmatching.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

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
        } catch (NumberFormatException e) {
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

    @Override
    public Long getCounselorLastFinished(Long counselorId) {
        Object val = redisTemplate.opsForValue().get(RedisKeyManager.counselorLastFinished(counselorId));
        if (val == null) return null;
        try {
            return Long.valueOf(val.toString());
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
                .add(RedisKeyManager.categoryCounselors(categoryId), counselorId);
    }

    @Override
    public void removeCounselorFromCategory(Long categoryId, Long counselorId) {
        redisTemplate.opsForSet()
                .remove(RedisKeyManager.categoryCounselors(categoryId), counselorId);
    }

    @Override
    public Set<Object> getCounselorsOfCategory(Long categoryId) {
        return redisTemplate.opsForSet().members(RedisKeyManager.categoryCounselors(categoryId));
    }


    // ==========================================
    // 카테고리 Queue
    // ==========================================
    @Override
    public void enqueueSession(Long categoryId, Long sessionId) {
        redisTemplate.opsForList()
                .rightPush(RedisKeyManager.categoryQueue(categoryId), sessionId.toString());
    }

    @Override
    public Long dequeueSession(Long categoryId) {
        Object value = redisTemplate.opsForList()
                .leftPop(RedisKeyManager.categoryQueue(categoryId));
        if (value == null) return null;

        try {
            return Long.valueOf(value.toString());
        } catch (Exception e) {
            log.error("[RedisRepo] dequeue 변환 실패: {}", value);
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
                    0,                                 // 모든 위치에서 제거
                    String.valueOf(sessionId)          // enqueue 시 toString() 했으므로 동일하게
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
        redisTemplate.opsForValue().set(RedisKeyManager.sessionStatus(sessionId), status);
    }

    @Override
    public String getSessionStatus(Long sessionId) {
        Object v = redisTemplate.opsForValue().get(RedisKeyManager.sessionStatus(sessionId));
        return v != null ? v.toString() : null;
    }

    @Override
    public void setSessionUser(Long sessionId, Long userId) {
        redisTemplate.opsForValue().set(RedisKeyManager.sessionUser(sessionId), userId);
    }

    @Override
    public Long getSessionUser(Long sessionId) {
        Object v = redisTemplate.opsForValue().get(RedisKeyManager.sessionUser(sessionId));
        return v != null ? Long.valueOf(v.toString()) : null;
    }

    @Override
    public void setSessionCounselor(Long sessionId, Long counselorId) {
        redisTemplate.opsForValue().set(RedisKeyManager.sessionCounselor(sessionId), counselorId);
    }

    @Override
    public Long getSessionCounselor(Long sessionId) {
        Object v = redisTemplate.opsForValue().get(RedisKeyManager.sessionCounselor(sessionId));
        return v != null ? Long.valueOf(v.toString()) : null;
    }

    @Override
    public void setSessionCategory(Long sessionId, Long categoryId) {
        redisTemplate.opsForValue().set(RedisKeyManager.sessionCategory(sessionId), categoryId);
    }

    @Override
    public Long getSessionCategory(Long sessionId) {
        Object v = redisTemplate.opsForValue().get(RedisKeyManager.sessionCategory(sessionId));
        return v != null ? Long.valueOf(v.toString()) : null;
    }

    @Override
    public void deleteSessionKeys(Long sessionId) {
        try {
            redisTemplate.delete(RedisKeyManager.sessionStatus(sessionId));
            redisTemplate.delete(RedisKeyManager.sessionUser(sessionId));
            redisTemplate.delete(RedisKeyManager.sessionCounselor(sessionId));
            redisTemplate.delete(RedisKeyManager.sessionCategory(sessionId));
            // 필요하면 메시지 리스트, 기타 키도 여기에서 함께 삭제
            // redisTemplate.delete(RedisKeyManager.sessionMessages(sessionId));
        } catch (Exception e) {
            log.error("[RedisRepo] deleteSessionKeys 실패: sessionId={}", sessionId, e);
        }
    }


    // ==========================================
    // WAITING 세션 조회 (userId 기준)
    // ==========================================
    @Override
    public Long findWaitingSessionByUser(Long userId) {
        Set<String> keys = redisTemplate.keys("session:*:userId");
        if (keys == null || keys.isEmpty()) return null;

        String userStr = String.valueOf(userId);

        for (String key : keys) {
            Object val = redisTemplate.opsForValue().get(key);
            if (val == null) continue;

            if (!userStr.equals(val.toString())) continue;

            Long sessionId = extractSessionId(key);
            if (sessionId == null) continue;

            String status = getSessionStatus(sessionId);
            if ("WAITING".equals(status)) {
                return sessionId;
            }
        }

        return null;
    }

    private Long extractSessionId(String key) {
        try {
            String[] parts = key.split(":"); // session:{id}:userId
            return Long.valueOf(parts[1]);
        } catch (Exception e) {
            log.warn("[RedisRepo] sessionId 파싱 실패: key={}", key);
            return null;
        }
    }


    // ==========================================
    // WebSocket Channel Pub/Sub
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
