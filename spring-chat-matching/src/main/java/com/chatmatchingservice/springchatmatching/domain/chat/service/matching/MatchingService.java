package com.chatmatchingservice.springchatmatching.domain.chat.service.matching;

import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisKeyManager;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

/**
 * 상담사 매칭 서비스
 *
 * - Redis 기반으로 상담사 후보를 조회하고
 * - load, lastFinishedAt 기준으로 우선순위 결정
 * - 대기열에서 세션을 꺼내 매칭
 * - DB 및 Redis 상태 반영
 *
 * Strategy 패턴:
 *  - 매칭 알고리즘(정렬 기준)을 별도 전략으로 분리해두어
 *    향후 load 우선, 점수 우선, AI 점수 기반 등으로 교체 가능
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatSessionRepository chatSessionRepository;

    /**
     * 매칭 알고리즘 전략 인터페이스 (Strategy 패턴)
     * -> 현재는 기본 구현(Load 우선)만 사용하지만, 추후 다른 정책을 쉽게 추가 가능
     */
    @FunctionalInterface
    public interface MatchingAlgorithm {
        CounselorCandidate select(List<CounselorCandidate> candidates);
    }

    /**
     * 기본 전략 구현: load → lastFinishedAt 기준으로 정렬해서 최소값 선택
     */
    private final MatchingAlgorithm matchingAlgorithm = candidates -> {
        if (candidates == null || candidates.isEmpty()) return null;

        candidates.sort(
                Comparator.comparingInt(CounselorCandidate::load)
                        .thenComparingLong(CounselorCandidate::lastFinishedAt)
        );
        return candidates.get(0);
    };

    /**
     * 카테고리별 매칭 시도
     *
     * 1) categoryCounselors Set에서 상담사 ID 목록 조회
     * 2) ONLINE / AFTER_CALL 상태만 후보로 필터링
     * 3) load / lastFinishedAt 기준 전략(Strategy)으로 상담사 1명 선택
     * 4) queue:category:{id} 리스트에서 sessionId pop
     * 5) DB의 chat_session에 counselor 배정
     * 6) Redis 상태 업데이트 (load, status, session 메타)
     * 7) Pub/Sub으로 ASSIGNED 이벤트 전송
     */
    @Transactional
    public void tryMatch(long categoryId) {

        try {
            // 1) 카테고리별 상담사 Set 조회
            Set<Object> ids = redisTemplate.opsForSet()
                    .members(RedisKeyManager.categoryCounselors(categoryId));

            if (ids == null || ids.isEmpty()) {
                log.debug("[Matching] categoryId={} 상담사 Set 비어 있음", categoryId);
                return;
            }

            List<CounselorCandidate> candidates = new ArrayList<>();

            // 2) 후보 필터링 (ONLINE / AFTER_CALL 상태만)
            for (Object rawId : ids) {
                Long id = parseLongOrNull(rawId);
                if (id == null) {
                    log.warn("[Matching] 잘못된 counselor id 값: {}", rawId);
                    continue;
                }

                String status = getStringSafely(RedisKeyManager.counselorStatus(id));
                if (!"ONLINE".equals(status) && !"AFTER_CALL".equals(status)) {
                    continue;
                }

                int load = getIntSafely(RedisKeyManager.counselorLoad(id), 0);
                long lastFinished = getLongSafely(RedisKeyManager.counselorLastFinished(id), 0L);

                candidates.add(new CounselorCandidate(id, load, lastFinished));
            }

            if (candidates.isEmpty()) {
                log.debug("[Matching] categoryId={} 조건 만족하는 상담사 없음", categoryId);
                return;
            }

            // 3) Strategy 패턴으로 상담사 1명 선택
            CounselorCandidate selected = matchingAlgorithm.select(candidates);
            if (selected == null) {
                log.debug("[Matching] categoryId={} 선택된 상담사 없음", categoryId);
                return;
            }

            // 4) 카테고리 대기열에서 세션 pop
            Object sidObj = redisTemplate.opsForList()
                    .leftPop(RedisKeyManager.categoryQueue(categoryId));

            if (sidObj == null) {
                log.debug("[Matching] categoryId={} 대기열 비어 있음", categoryId);
                return;
            }

            Long sessionId = parseLongOrNull(sidObj);
            if (sessionId == null) {
                log.warn("[Matching] 잘못된 sessionId 값: {}", sidObj);
                return;
            }

            // 5) DB 반영 (세션에 상담사 배정)
            try {
                chatSessionRepository.assignCounselor(sessionId, selected.counselorId());
            } catch (DataAccessException e) {
                log.error("[Matching] DB assignCounselor 실패: sessionId={}, counselorId={}",
                        sessionId, selected.counselorId(), e);
                // DB 반영 실패 시 Redis 상태를 건드리지 않고 종료
                return;
            }

            // 6) Redis 상태 업데이트
            redisTemplate.opsForValue()
                    .increment(RedisKeyManager.counselorLoad(selected.counselorId()), 1);
            redisTemplate.opsForValue()
                    .set(RedisKeyManager.counselorStatus(selected.counselorId()), "BUSY");

            redisTemplate.opsForValue()
                    .set(RedisKeyManager.sessionStatus(sessionId), "IN_PROGRESS");
            redisTemplate.opsForValue()
                    .set(RedisKeyManager.sessionCounselor(sessionId), selected.counselorId());

            // 7) Pub/Sub: ASSIGNED 이벤트 전송
            WSMessage assigned = new WSMessage(
                    "ASSIGNED",
                    String.valueOf(sessionId),
                    "SYSTEM",
                    selected.counselorId(),
                    "상담사가 배정되었습니다.",
                    Instant.now().toEpochMilli()
            );
            String channel = RedisKeyManager.wsChannel(sessionId);
            redisTemplate.convertAndSend(channel, assigned);

            log.info("[Matching] 매칭 성공: categoryId={}, sessionId={}, counselorId={}, load={}",
                    categoryId, sessionId, selected.counselorId(), selected.load());

        } catch (Exception e) {
            log.error("[Matching] tryMatch 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 상담 종료 후 처리: load 감소, lastFinishedAt 업데이트, AFTER_CALL 상태 설정
     * (Template Method 패턴의 일부는 EndSessionTemplate에서 처리)
     */
    public void markSessionFinished(Long sessionId, long counselorId) {
        try {
            redisTemplate.opsForValue()
                    .increment(RedisKeyManager.counselorLoad(counselorId), -1);
            redisTemplate.opsForValue()
                    .set(RedisKeyManager.counselorLastFinished(counselorId),
                            String.valueOf(Instant.now().toEpochMilli()));
            redisTemplate.opsForValue()
                    .set(RedisKeyManager.counselorStatus(counselorId), "AFTER_CALL");

            redisTemplate.opsForValue()
                    .set(RedisKeyManager.sessionStatus(sessionId), "AFTER_CALL");

            log.info("[Matching] 세션 종료 처리: sessionId={}, counselorId={}",
                    sessionId, counselorId);

        } catch (Exception e) {
            log.error("[Matching] markSessionFinished 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    // ---- 내부 유틸 메서드 ----

    private Long parseLongOrNull(Object value) {
        try {
            return value == null ? null : Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getStringSafely(String key) {
        Object v = redisTemplate.opsForValue().get(key);
        return v == null ? null : v.toString();
    }

    private int getIntSafely(String key, int defaultValue) {
        Object v = redisTemplate.opsForValue().get(key);
        if (v == null) return defaultValue;
        try {
            return Integer.parseInt(v.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private long getLongSafely(String key, long defaultValue) {
        Object v = redisTemplate.opsForValue().get(key);
        if (v == null) return defaultValue;
        try {
            return Long.parseLong(v.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 상담사 후보 정보를 담는 내부 record
     */
    private record CounselorCandidate(long counselorId, int load, long lastFinishedAt) {}
    public record MatchingAssignedMessage(String type, Long sessionId, long counselorId) {}
}
