package domain.counselor.service;

import domain.chat.service.MatchingService;
import domain.counselor.dto.CounselorStatusUpdateRequest;
import domain.counselor.entity.CounselorStatus;
import infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CounselorStatusService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MatchingService matchingService;

    @Transactional
    public void updateStatus(long counselorId, CounselorStatusUpdateRequest req) {

        CounselorStatus status = req.getStatus();
        Long categoryId = req.getCategoryId();

        // 상태 저장
        redisTemplate.opsForValue().set(RedisKeyManager.counselorStatus(counselorId), status.name());

        // OFFLINE → 카테고리 SET 제거
        if (status == CounselorStatus.OFFLINE) {
            if (categoryId != null)
                redisTemplate.opsForSet().remove(RedisKeyManager.categoryCounselors(categoryId), counselorId);
            return;
        }

        // ONLINE / AFTER_CALL → 카테고리 SET 등록
        if (categoryId != null)
            redisTemplate.opsForSet().add(RedisKeyManager.categoryCounselors(categoryId), counselorId);

        // READY 상태면 매칭 시도
        if (status == CounselorStatus.ONLINE || status == CounselorStatus.AFTER_CALL)
            matchingService.tryMatch(categoryId);
    }
}
