package com.chatmatchingservice.springchatmatching.domain.counselor.repository;

import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CounselorSkillRepository extends JpaRepository<CounselorSkill, Long> {
    List<CounselorSkill> findByCounselorId(Long counselorId);
    List<CounselorSkill> findByCategoryId(Long categoryId);
}
