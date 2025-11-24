package domain.counselor.service;

import domain.chat.entity.ChatSession;
import domain.chat.repository.ChatSessionRepository;
import domain.chat.service.MatchingService;
import domain.counselor.dto.CounselRequestDto;
import infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitingRequestService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatSessionRepository chatSessionRepository;
    private final MatchingService matchingService;

    public Long enqueue(CounselRequestDto dto) {

        Long categoryId = dto.categoryId();
        Long userId = dto.userId();

        // DB WAITING 세션 생성
        ChatSession session = chatSessionRepository.createWaitingSession(userId, categoryId);
        Long sessionId = session.getId();

        // Redis Queue push
        redisTemplate.opsForList()
                .rightPush(RedisKeyManager.categoryQueue(categoryId), sessionId.toString());

        // 세션 메타데이터 적재
        redisTemplate.opsForValue().set(RedisKeyManager.sessionStatus(sessionId), "WAITING");
        redisTemplate.opsForValue().set(RedisKeyManager.sessionUser(sessionId), userId);
        redisTemplate.opsForValue().set(RedisKeyManager.sessionCategory(sessionId), categoryId);

        // 매칭 시도
        matchingService.tryMatch(categoryId);

        return sessionId;
    }
}
