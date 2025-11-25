package com.chatmatchingservice.springchatmatching.domain.counselor.service;

import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorSkill;
import com.chatmatchingservice.springchatmatching.domain.counselor.repository.CounselorSkillRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CounselorSkillService {

    private final CounselorSkillRepository skillRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void addSkill(Long counselorId, Long categoryId) {
        skillRepository.save(new CounselorSkill(counselorId, categoryId));

        // Redis SET 에 상담사 추가
        redisTemplate.opsForSet().add(
                RedisKeyManager.categoryCounselors(categoryId),
                counselorId
        );
    }

    public void removeSkill(Long skillId) {
        CounselorSkill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        skillRepository.delete(skill);

        // Redis SET 에서 삭제
        redisTemplate.opsForSet().remove(
                RedisKeyManager.categoryCounselors(skill.getCategoryId()),
                skill.getCounselorId()
        );
    }
}
