package domain.counselor.service;


import domain.chat.repository.ChatSessionRepository;
import domain.counselor.dto.CounselRequestDto;
import infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WaitingRequestService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatSessionRepository chatSessionRepository;

    public String enqueue(CounselRequestDto dto) {
        Long categoryId = dto.categoryId();

        // 세션 ID 생성 (DB PK를 쓰거나, UUID를 쓰거나 자유)
        String sessionId = UUID.randomUUID().toString();

        // DB에 WAITING 상태 세션 생성 (필요시)
        chatSessionRepository.createWaitingSession(sessionId, dto.userId(), categoryId);

        // Redis 대기열에 push
        String queueKey = RedisKeyManager.categoryQueue(categoryId);
        redisTemplate.opsForList().rightPush(queueKey, sessionId);

        // 세션 상태도 Redis에 적재
        redisTemplate.opsForValue().set(RedisKeyManager.sessionStatus(sessionId), "WAITING");

        return sessionId;
    }
}