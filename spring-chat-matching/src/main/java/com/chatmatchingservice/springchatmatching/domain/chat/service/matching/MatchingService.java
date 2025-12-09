package com.chatmatchingservice.springchatmatching.domain.chat.service.matching;

import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.websocket.MessageFactory;
import com.chatmatchingservice.springchatmatching.domain.chat.service.message.MessageHandler;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final RedisRepository redisRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final MessageFactory messageFactory;

    @FunctionalInterface
    public interface MatchingAlgorithm {
        CounselorCandidate select(List<CounselorCandidate> candidates);
    }

    private final MatchingAlgorithm matchingAlgorithm = candidates -> {
        if (candidates == null || candidates.isEmpty()) return null;

        candidates.sort(
                Comparator.comparingInt(CounselorCandidate::load)
                        .thenComparingLong(CounselorCandidate::lastFinishedAt)
        );
        return candidates.get(0);
    };

    @Transactional
    public void tryMatch(long categoryId) {

        try {
            Set<String> ids = redisRepository.getCounselorsOfCategory(categoryId);

            if (ids == null || ids.isEmpty()) {
                log.debug("[Matching] categoryId={} 상담사 없음", categoryId);
                return;
            }

            List<CounselorCandidate> candidates = new ArrayList<>();

            for (Object rawId : ids) {
                Long id = parseLongOrNull(rawId);
                if (id == null) continue;

                String status = redisRepository.getCounselorStatus(id);

                // 🔥 [CHANGE POINT #1] READY 만 매칭 대상
                if (!"READY".equals(status)) {
                    log.debug("[Matching] counselorId={} 상태={} → 매칭 대상 제외", id, status);
                    continue;
                }

                int load = (int) redisRepository.getCounselorLoad(id);
                long lastFinished = Optional.ofNullable(redisRepository.getCounselorLastFinished(id))
                        .orElse(0L);

                candidates.add(new CounselorCandidate(id, load, lastFinished));
            }

            if (candidates.isEmpty()) {
                log.debug("[Matching] categoryId={} 매칭 가능한 READY 상담사 없음", categoryId);
                return;
            }

            CounselorCandidate selected = matchingAlgorithm.select(candidates);
            if (selected == null) return;

            Long sessionId = redisRepository.dequeueSession(categoryId);
            if (sessionId == null) {
                log.debug("[Matching] categoryId={} 대기열 비어 있음", categoryId);
                return;
            }

            try {
                chatSessionRepository.assignCounselor(sessionId, selected.counselorId());
                chatSessionRepository.markSessionStarted(sessionId);
            } catch (DataAccessException e) {
                log.error("[Matching] DB assignCounselor 실패", e);
                return;
            }

            // 🔥 [CHANGE POINT #2] 매칭된 상담사는 BUSY로 변경
            redisRepository.incrementCounselorLoad(selected.counselorId(), 1);
            redisRepository.setCounselorStatus(selected.counselorId(), "BUSY");

            redisRepository.setSessionStatus(sessionId, "IN_PROGRESS");
            redisRepository.setSessionCounselor(sessionId, selected.counselorId());

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

    private Long parseLongOrNull(Object value) {
        try { return value == null ? null : Long.parseLong(value.toString()); }
        catch (NumberFormatException e) { return null; }
    }

    private record CounselorCandidate(long counselorId, int load, long lastFinishedAt) {}

    public record MatchingAssignedMessage(String type, Long sessionId, long counselorId) {}
}
