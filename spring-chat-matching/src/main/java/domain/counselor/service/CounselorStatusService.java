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

        // 기본 상태 변경
        String statusKey = RedisKeyManager.counselorStatus(counselorId);
        redisTemplate.opsForValue().set(statusKey, status.name());

        if (status == CounselorStatus.OFFLINE) {
            // 필요하면 category:*:counselors 에서 제거
            if (categoryId != null) {
                String setKey = RedisKeyManager.categoryCounselors(categoryId);
                redisTemplate.opsForSet().remove(setKey, String.valueOf(counselorId));
            }
            return;
        }

        // ONLINE / AFTER_CALL 이면 카테고리 SET에 추가
        if (categoryId != null) {
            String setKey = RedisKeyManager.categoryCounselors(categoryId);
            redisTemplate.opsForSet().add(setKey, String.valueOf(counselorId));
        }

        // READY(ONLINE / AFTER_CALL) 상태면 매칭 시도
        if (status == CounselorStatus.ONLINE || status == CounselorStatus.AFTER_CALL) {
            if (categoryId != null) {
                matchingService.tryMatch(categoryId);
            }
        }
    }
}