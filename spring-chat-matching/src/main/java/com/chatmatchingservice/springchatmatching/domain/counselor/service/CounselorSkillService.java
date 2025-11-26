package com.chatmatchingservice.springchatmatching.domain.counselor.service;

import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorSkill;
import com.chatmatchingservice.springchatmatching.domain.counselor.repository.CounselorSkillRepository;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CounselorSkillService {

    private final CounselorSkillRepository skillRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 상담사의 상담 가능 카테고리 추가
     */
    public void addSkill(Long counselorId, Long categoryId) {
        try {
            skillRepository.save(new CounselorSkill(counselorId, categoryId));

            // Redis SET 추가
            redisTemplate.opsForSet().add(
                    RedisKeyManager.categoryCounselors(categoryId),
                    counselorId
            );

            log.info("[Skill] ADD: counselorId={}, categoryId={}", counselorId, categoryId);

        } catch (Exception e) {
            log.error("[Skill] ADD 처리 중 예외: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 상담사의 상담 가능 카테고리 제거
     */
    public void removeSkill(Long skillId) {
        try {
            CounselorSkill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            skillRepository.delete(skill);

            // Redis SET 에서 제거
            redisTemplate.opsForSet().remove(
                    RedisKeyManager.categoryCounselors(skill.getCategoryId()),
                    skill.getCounselorId()
            );

            log.info("[Skill] REMOVE: skillId={}, counselorId={}, categoryId={}",
                    skillId, skill.getCounselorId(), skill.getCategoryId());

        } catch (CustomException e) {
            throw e; // 그대로 전달

        } catch (Exception e) {
            log.error("[Skill] REMOVE 처리 중 예외: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
