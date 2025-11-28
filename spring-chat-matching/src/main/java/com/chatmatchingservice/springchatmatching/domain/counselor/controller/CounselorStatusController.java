package com.chatmatchingservice.springchatmatching.domain.counselor.controller;

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
    public ResponseEntity<String> ready(Authentication auth) {

        Long counselorId = (Long) auth.getPrincipal();

        log.info("[API] Counselor → READY : id={}", counselorId);

        counselorStatusService.updateStatus(
                counselorId,
                new CounselorStatusUpdateRequest(
                        counselorId,
                        CounselorStatus.ONLINE,
                        null
                )
        );

        return ResponseEntity.ok("READY");
    }

    // =========================
    // BUSY
    // =========================
    @PatchMapping("/busy")
    public ResponseEntity<String> busy(Authentication auth) {

        Long counselorId = (Long) auth.getPrincipal();
        log.info("[API] Counselor → BUSY : id={}", counselorId);

        counselorStatusService.updateStatus(
                counselorId,
                new CounselorStatusUpdateRequest(
                        counselorId,
                        CounselorStatus.BUSY,
                        null
                )
        );

        return ResponseEntity.ok("BUSY");
    }


    // =========================
    // AFTER_CALL (후처리)
    // =========================
    @PatchMapping("/after-call")
    public ResponseEntity<String> afterCall(Authentication auth) {

        Long counselorId = (Long) auth.getPrincipal();
        log.info("[API] Counselor → AFTER_CALL : id={}", counselorId);

        counselorStatusService.setAfterCall(counselorId);

        return ResponseEntity.ok("AFTER_CALL");
    }


    // =========================
    // OFFLINE
    // =========================
    @PatchMapping("/offline")
    public ResponseEntity<String> offline(Authentication auth) {

        Long counselorId = (Long) auth.getPrincipal();
        log.info("[API] Counselor → OFFLINE : id={}", counselorId);

        counselorStatusService.updateStatus(
                counselorId,
                new CounselorStatusUpdateRequest(
                        counselorId,
                        CounselorStatus.OFFLINE,
                        null
                )
        );

        return ResponseEntity.ok("OFFLINE");
    }
}
