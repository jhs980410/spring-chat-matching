package com.chatmatchingservice.springchatmatching.domain.chat.service.matching;

import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.websocket.MessageFactory;
import com.chatmatchingservice.springchatmatching.domain.chat.service.message.MessageHandler;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;   // 🔥 추가됨
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

/**
 * 상담사 매칭 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final RedisRepository redisRepository;     //  RedisTemplate 제거 → RedisRepository로 변경
    private final ChatSessionRepository chatSessionRepository;
    private final MessageFactory messageFactory;

    /**
     * 매칭 알고리즘 전략 인터페이스
     */
    @FunctionalInterface
    public interface MatchingAlgorithm {
        CounselorCandidate select(List<CounselorCandidate> candidates);
    }

    /**
     * 기본 전략 구현 (load → lastFinishedAt)
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
     */
    @Transactional
    public void tryMatch(long categoryId) {

        try {
            // -----------------------------------
            // 1) 카테고리 상담사 Set 조회
            // -----------------------------------
            Set<Object> ids = redisRepository.getCounselorsOfCategory(categoryId);

            if (ids == null || ids.isEmpty()) {
                log.debug("[Matching] categoryId={} 상담사 없음", categoryId);
                return;
            }

            List<CounselorCandidate> candidates = new ArrayList<>();

            // -----------------------------------
            // 2) ONLINE / AFTER_CALL 상담사 필터링
            // -----------------------------------
            for (Object rawId : ids) {
                Long id = parseLongOrNull(rawId);
                if (id == null) continue;

                String status = redisRepository.getCounselorStatus(id);
                if (!"ONLINE".equals(status) && !"AFTER_CALL".equals(status)) continue;

                int load = (int) redisRepository.getCounselorLoad(id);
                long lastFinished = Optional.ofNullable(redisRepository.getCounselorLastFinished(id))
                        .orElse(0L);

                candidates.add(new CounselorCandidate(id, load, lastFinished));
            }

            if (candidates.isEmpty()) {
                log.debug("[Matching] categoryId={} 매칭 가능 상담사 없음", categoryId);
                return;
            }

            // -----------------------------------
            // 3) Strategy 패턴으로 상담사 선택
            // -----------------------------------
            CounselorCandidate selected = matchingAlgorithm.select(candidates);
            if (selected == null) return;

            // -----------------------------------
            // 4) 대기열 POP
            // -----------------------------------
            Long sessionId = redisRepository.dequeueSession(categoryId);
            if (sessionId == null) {
                log.debug("[Matching] categoryId={} 대기열 비어 있음", categoryId);
                return;
            }

            // -----------------------------------
            // 5) DB 반영
            // -----------------------------------
            try {
                chatSessionRepository.assignCounselor(sessionId, selected.counselorId());
            } catch (DataAccessException e) {
                log.error("[Matching] DB assignCounselor 실패", e);
                return;
            }

            // -----------------------------------
            // 6) Redis 상태 업데이트
            // -----------------------------------
            redisRepository.incrementCounselorLoad(selected.counselorId(), 1);
            redisRepository.setCounselorStatus(selected.counselorId(), "BUSY");

            redisRepository.setSessionStatus(sessionId, "IN_PROGRESS");
            redisRepository.setSessionCounselor(sessionId, selected.counselorId());

            // --------------------------------------------------------------
            // 7) Pub/Sub — ASSIGNED 메시지를 Handler로 전달
            // --------------------------------------------------------------
            WSMessage assigned = new WSMessage(
                    "ASSIGNED",
                    String.valueOf(sessionId),
                    "SYSTEM",
                    selected.counselorId(),
                    "상담사가 배정되었습니다.",
                    Instant.now().toEpochMilli()
            );

            MessageHandler handler = messageFactory.getHandler(assigned);
            handler.handle(assigned);

            log.info("[Matching] 매칭 성공: categoryId={}, sessionId={}, counselorId={}",
                    categoryId, sessionId, selected.counselorId());

        } catch (Exception e) {
            log.error("[Matching] tryMatch 중 예외", e);
        }
    }

    /**
     * 상담 종료 후 처리
     */
    public void markSessionFinished(Long sessionId, long counselorId) {
        try {
            redisRepository.incrementCounselorLoad(counselorId, -1);
            redisRepository.setCounselorLastFinished(counselorId, Instant.now().toEpochMilli());
            redisRepository.setCounselorStatus(counselorId, "AFTER_CALL");

            redisRepository.setSessionStatus(sessionId, "AFTER_CALL");

            log.info("[Matching] sessionId={} 종료 처리 완료", sessionId);

        } catch (Exception e) {
            log.error("[Matching] markSessionFinished 예외", e);
        }
    }

    // ---- 내부 유틸 메서드 ----
    private Long parseLongOrNull(Object value) {
        try { return value == null ? null : Long.parseLong(value.toString()); }
        catch (NumberFormatException e) { return null; }
    }

    /**
     * 상담사 후보 정보를 담는 내부 record
     */
    private record CounselorCandidate(long counselorId, int load, long lastFinishedAt) {}

    public record MatchingAssignedMessage(String type, Long sessionId, long counselorId) {}
}
