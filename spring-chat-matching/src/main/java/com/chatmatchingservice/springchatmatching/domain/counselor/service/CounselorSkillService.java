package com.chatmatchingservice.springchatmatching.domain.counselor.service;

import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorSkillRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorSkill;
import com.chatmatchingservice.springchatmatching.domain.counselor.repository.CounselorSkillRepository;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CounselorSkillService {

    private final CounselorSkillRepository skillRepository;
    private final RedisRepository redisRepository;   // 🔥 RedisTemplate → RedisRepository

    /**
     * 상담사의 상담 가능 카테고리 추가
     */
    public void addSkill(Long counselorId, CounselorSkillRequest req) {
        try {
            // 1) DB 저장
            skillRepository.save(new CounselorSkill(counselorId, req.categoryId()));

            // 2) Redis SET 추가 (Repository 사용)
            redisRepository.addCounselorToCategory(req.categoryId(), counselorId);

            log.info("[Skill] ADD: counselorId={}, categoryId={}", counselorId, req.categoryId());

        } catch (CustomException e) {
            throw e;

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
            // 1) DB 조회
            CounselorSkill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            // 2) DB 삭제
            skillRepository.delete(skill);

            // 3) Redis SET 제거
            redisRepository.removeCounselorFromCategory(skill.getCategoryId(), skill.getCounselorId());

            log.info("[Skill] REMOVE: skillId={}, counselorId={}, categoryId={}",
                    skillId, skill.getCounselorId(), skill.getCategoryId());

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            log.error("[Skill] REMOVE 처리 중 예외: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
