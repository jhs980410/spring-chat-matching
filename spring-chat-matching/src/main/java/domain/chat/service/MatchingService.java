package domain.chat.service;

import domain.chat.repository.ChatSessionRepository;
import infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatSessionRepository chatSessionRepository; // DB에 세션 배정 반영용

    /**
     * 상담사가 READY(ONLINE/AFTER_CALL) 상태가 될 때 호출.
     * categoryId 기준으로 대기열에서 하나 꺼내서 해당 상담사로 배정 or
     * 전체 후보 중 최적 상담사 선택 로직.
     */
    @Transactional
    public void tryMatch(long categoryId) {
        // 1) 카테고리별 상담사 후보 SET 조회
        String key = RedisKeyManager.categoryCounselors(categoryId);
        Set<Object> ids = redisTemplate.opsForSet().members(key);
        if (ids == null || ids.isEmpty()) return;

        List<CounselorCandidate> candidates = new ArrayList<>();

        // 2) 상태/로드 조회 후 필터링
        for (Object rawId : ids) {
            long counselorId = Long.parseLong(rawId.toString());

            String statusKey = RedisKeyManager.counselorStatus(counselorId);
            String loadKey = RedisKeyManager.counselorLoad(counselorId);
            String lastFinishedKey = RedisKeyManager.counselorLastFinished(counselorId);

            Object statusObj = redisTemplate.opsForValue().get(statusKey);
            Object loadObj = redisTemplate.opsForValue().get(loadKey);
            Object lastFinishedObj = redisTemplate.opsForValue().get(lastFinishedKey);

            String status = statusObj == null ? "OFFLINE" : statusObj.toString();
            if (!status.equals("ONLINE") && !status.equals("AFTER_CALL")) {
                continue;
            }

            int load = loadObj == null ? 0 : Integer.parseInt(loadObj.toString());
            long lastFinishedAt = lastFinishedObj == null
                    ? 0L
                    : Long.parseLong(lastFinishedObj.toString());

            candidates.add(new CounselorCandidate(counselorId, load, lastFinishedAt));
        }

        if (candidates.isEmpty()) {
            return;
        }

        // 3) load → lastFinishedAt 기준 정렬
        candidates.sort((a, b) -> {
            if (a.load != b.load) return Integer.compare(a.load, b.load);
            return Long.compare(a.lastFinishedAt, b.lastFinishedAt);
        });

        CounselorCandidate selected = candidates.get(0);

        // 4) 대기열에서 세션 POP
        String queueKey = RedisKeyManager.categoryQueue(categoryId);
        Object sessionIdObj = redisTemplate.opsForList().leftPop(queueKey);
        if (sessionIdObj == null) {
            // 대기열이 비어 있으면 할 일 없음
            return;
        }
        String sessionId = sessionIdObj.toString();

        // 5) DB 반영 (세션 배정)
        chatSessionRepository.assignCounselor(sessionId, selected.counselorId);

        // 6) Redis 상태 업데이트
        String loadKey = RedisKeyManager.counselorLoad(selected.counselorId);
        String statusKey = RedisKeyManager.counselorStatus(selected.counselorId);
        redisTemplate.opsForValue().increment(loadKey, 1);
        redisTemplate.opsForValue().set(statusKey, "BUSY");

        String sessionStatusKey = RedisKeyManager.sessionStatus(sessionId);
        String sessionCounselorKey = RedisKeyManager.sessionCounselor(sessionId);
        redisTemplate.opsForValue().set(sessionStatusKey, "IN_PROGRESS");
        redisTemplate.opsForValue().set(sessionCounselorKey, String.valueOf(selected.counselorId));

        // 7) Pub/Sub 통해 WebSocket으로 배정 이벤트 날리기
        MatchingAssignedMessage message = new MatchingAssignedMessage(
                "ASSIGNED",
                sessionId,
                selected.counselorId
        );
        redisTemplate.convertAndSend(RedisKeyManager.wsChannel(sessionId), message);
    }

    public void markSessionFinished(String sessionId, long counselorId) {
        // 세션 종료 시 load 감소 + lastFinishedAt 갱신
        String loadKey = RedisKeyManager.counselorLoad(counselorId);
        String statusKey = RedisKeyManager.counselorStatus(counselorId);
        String lastFinishedKey = RedisKeyManager.counselorLastFinished(counselorId);

        redisTemplate.opsForValue().increment(loadKey, -1);
        redisTemplate.opsForValue().set(lastFinishedKey, String.valueOf(Instant.now().toEpochMilli()));
        redisTemplate.opsForValue().set(statusKey, "AFTER_CALL");
    }

    private record CounselorCandidate(long counselorId, int load, long lastFinishedAt) {}

    public record MatchingAssignedMessage(
            String type,
            String sessionId,
            long counselorId
    ) {}
}
