package com.chatmatchingservice.springchatmatching.domain.counselor.controller;

import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorReadyRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorStatusUpdateRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.entity.CounselorStatus;
import com.chatmatchingservice.springchatmatching.domain.counselor.service.CounselorStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/counselors")
public class CounselorStatusController {

    private final CounselorStatusService counselorStatusService;

    // =========================
    // READY (ONLINE)
    // =========================
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
    @PatchMapping("/after-call")
    public ResponseEntity<String> afterCall(Authentication auth) {

        Long counselorId = (Long) auth.getPrincipal();

        counselorStatusService.setAfterCall(counselorId);

        return ResponseEntity.ok("AFTER_CALL");
    }

    // =========================
    // OFFLINE
    // =========================
    @PatchMapping("/offline")
    public ResponseEntity<String> offline(Authentication auth) {

        Long counselorId = (Long) auth.getPrincipal();

        counselorStatusService.offline(counselorId);

        return ResponseEntity.ok("OFFLINE");
    }
}
