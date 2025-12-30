package com.chatmatchingservice.springchatmatching.domain.counselor.controller;

import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorSkillRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.service.CounselorSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Counselor Skill",
        description = """
    상담사 스킬 관리 API

    - 상담사가 보유한 상담 카테고리(스킬) 관리
    - 매칭 시 상담사 후보군 판단 기준
    """
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/counselors/skills")
@RequiredArgsConstructor
@Slf4j
public class CounselorSkillController {

    private final CounselorSkillService skillService;

    // ================================
    // 스킬 추가
    // ================================
    @Operation(
            summary = "상담사 스킬 추가",
            description = """
        상담사에게 상담 가능 카테고리(스킬)를 추가하는 API

        - 매칭 후보 카테고리 확장
        """
    )
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
    @Operation(
            summary = "상담사 스킬 삭제",
            description = "상담사의 상담 가능 카테고리(스킬)를 삭제하는 API"
    )
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
