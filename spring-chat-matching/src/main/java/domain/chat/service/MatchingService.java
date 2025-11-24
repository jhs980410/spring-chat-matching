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
    private final ChatSessionRepository chatSessionRepository;

    @Transactional
    public void tryMatch(long categoryId) {

        Set<Object> ids = redisTemplate.opsForSet()
                .members(RedisKeyManager.categoryCounselors(categoryId));
        if (ids == null || ids.isEmpty()) return;

        List<CounselorCandidate> candidates = new ArrayList<>();

        // 후보 필터링
        for (Object rawId : ids) {
            long id = Long.parseLong(rawId.toString());

            String status = (String) redisTemplate.opsForValue()
                    .get(RedisKeyManager.counselorStatus(id));

            if (!"ONLINE".equals(status) && !"AFTER_CALL".equals(status)) continue;

            int load = Integer.parseInt(
                    Optional.ofNullable(redisTemplate.opsForValue().get(RedisKeyManager.counselorLoad(id)))
                            .orElse("0").toString()
            );

            long lastFinished = Long.parseLong(
                    Optional.ofNullable(redisTemplate.opsForValue().get(RedisKeyManager.counselorLastFinished(id)))
                            .orElse("0").toString()
            );

            candidates.add(new CounselorCandidate(id, load, lastFinished));
        }

        if (candidates.isEmpty()) return;

        candidates.sort(Comparator.comparingInt(CounselorCandidate::load)
                .thenComparingLong(CounselorCandidate::lastFinishedAt));

        CounselorCandidate selected = candidates.get(0);

        // Queue pop
        Object sidObj = redisTemplate.opsForList()
                .leftPop(RedisKeyManager.categoryQueue(categoryId));
        if (sidObj == null) return;

        Long sessionId = Long.parseLong(sidObj.toString());

        // DB 반영
        chatSessionRepository.assignCounselor(sessionId, selected.counselorId);

        // Redis 업데이트
        redisTemplate.opsForValue().increment(RedisKeyManager.counselorLoad(selected.counselorId), 1);
        redisTemplate.opsForValue().set(RedisKeyManager.counselorStatus(selected.counselorId), "BUSY");

        redisTemplate.opsForValue().set(RedisKeyManager.sessionStatus(sessionId), "IN_PROGRESS");
        redisTemplate.opsForValue().set(RedisKeyManager.sessionCounselor(sessionId), selected.counselorId);

        // Pub/Sub
        redisTemplate.convertAndSend(
                RedisKeyManager.wsChannel(sessionId),
                new MatchingAssignedMessage("ASSIGNED", sessionId, selected.counselorId)
        );
    }

    public void markSessionFinished(Long sessionId, long counselorId) {
        redisTemplate.opsForValue().increment(RedisKeyManager.counselorLoad(counselorId), -1);
        redisTemplate.opsForValue().set(RedisKeyManager.counselorLastFinished(counselorId),
                String.valueOf(Instant.now().toEpochMilli()));
        redisTemplate.opsForValue().set(RedisKeyManager.counselorStatus(counselorId), "AFTER_CALL");
    }

    private record CounselorCandidate(long counselorId, int load, long lastFinishedAt) {}
    public record MatchingAssignedMessage(String type, Long sessionId, long counselorId) {}
}
