package com.chatmatchingservice.springchatmatching.domain.counselor.controller;

import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorSkillRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.service.CounselorSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counselors/skills")
@RequiredArgsConstructor
@Slf4j
public class CounselorSkillController {

    private final CounselorSkillService skillService;

    // ================================
    // 스킬 추가
    // ================================
    @PostMapping
    public ResponseEntity<Void> addSkill(
            Authentication auth,
            @RequestBody CounselorSkillRequest req
    ) {
        Long counselorId = (Long) auth.getPrincipal();
        log.info("[API] Counselor {} → Add Skill: {}", counselorId, req.categoryId());

        skillService.addSkill(counselorId, req);
        return ResponseEntity.ok().build();
    }

    // ================================
    // 스킬 삭제
    // ================================
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> removeSkill(
            Authentication auth,
            @PathVariable Long skillId
    ) {
        Long counselorId = (Long) auth.getPrincipal();
        log.info("[API] Counselor {} → Remove Skill {}", counselorId, skillId);

        skillService.removeSkill(skillId);
        return ResponseEntity.ok().build();
    }
}
