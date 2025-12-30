package com.chatmatchingservice.springchatmatching.domain.counselor.controller;

import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorReadyRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorStatusUpdateRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorStatus;
import com.chatmatchingservice.springchatmatching.domain.counselor.service.CounselorStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Counselor Status",
        description = """
    상담사 상태 관리 API

    - 상담사의 가용 상태 제어 (READY / BUSY / AFTER_CALL / OFFLINE)
    - READY 전환 시 매칭 로직이 주도적으로 트리거됨
    - 상담사 매칭 풀 진입/이탈 관리
    """
)
@SecurityRequirement(name = "bearerAuth")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/counselors")
public class CounselorStatusController {

    private final CounselorStatusService counselorStatusService;

    // =========================
    // READY (ONLINE)
    // =========================
    @Operation(
            summary = "상담사 READY 전환",
            description = """
    상담사를 상담 가능 상태(READY)로 전환하는 API

    - 상담사 상태를 READY로 변경
    - 담당 카테고리를 매칭 풀에 등록
    - 해당 카테고리 기준으로 매칭 로직을 주도적으로 트리거

    ※ 매칭의 핵심 트리거 API
    """
    )
    @PatchMapping("/ready")
    public ResponseEntity<String> ready(
            Authentication auth,
            @RequestBody CounselorReadyRequest request
    ) {
        Long counselorId = (Long) auth.getPrincipal();

        log.info("[API] Counselor READY: id={}, categories={}", counselorId, request.getCategoryIds());

        counselorStatusService.ready(counselorId, request.getCategoryIds());

        return ResponseEntity.ok("READY");
    }

    // =========================
    // BUSY
    // =========================
    @Operation(
            summary = "상담사 BUSY 전환",
            description = "상담사를 상담 중(BUSY) 상태로 전환하는 API"
    )
    @PatchMapping("/busy")
    public ResponseEntity<String> busy(Authentication auth) {

        Long counselorId = (Long) auth.getPrincipal();

        counselorStatusService.updateStatus(
                counselorId,
                new CounselorStatusUpdateRequest(counselorId, CounselorStatus.BUSY)
        );

        return ResponseEntity.ok("BUSY");
    }


    // =========================
    // AFTER_CALL
    // =========================
    @Operation(
            summary = "상담사 AFTER_CALL 전환",
            description = """
    상담 종료 후 후처리 상태(AFTER_CALL)로 전환하는 API

    - 상담 부하(load) 감소
    - 상담 종료 시점 기록
    """
    )
    @PatchMapping("/after-call")
    public ResponseEntity<String> afterCall(Authentication auth) {

        Long counselorId = (Long) auth.getPrincipal();

        counselorStatusService.setAfterCall(counselorId);

        return ResponseEntity.ok("AFTER_CALL");
    }

    // =========================
    // OFFLINE
    // =========================
    @Operation(
            summary = "상담사 OFFLINE 전환",
            description = """
    상담사를 OFFLINE 상태로 전환하는 API

    - 매칭 풀에서 상담사 제거
    - 모든 카테고리 매칭 후보에서 제외
    """
    )
    @PatchMapping("/offline")
    public ResponseEntity<String> offline(Authentication auth) {

        Long counselorId = (Long) auth.getPrincipal();

        counselorStatusService.offline(counselorId);

        return ResponseEntity.ok("OFFLINE");
    }
}
