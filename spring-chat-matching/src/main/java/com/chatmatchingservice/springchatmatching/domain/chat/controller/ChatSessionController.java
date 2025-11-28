package com.chatmatchingservice.springchatmatching.domain.chat.controller;

import com.chatmatchingservice.springchatmatching.domain.chat.dto.*;
import com.chatmatchingservice.springchatmatching.domain.chat.service.ChatSessionService;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselRequestDto;
import com.chatmatchingservice.springchatmatching.domain.counselor.service.WaitingRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sessions")
public class ChatSessionController {

    private final WaitingRequestService waitingRequestService;
    private final ChatSessionService chatSessionService;


    // ============================================
    // 1. 세션 생성 (유저 → WAITING)
    // ============================================
    @PostMapping
    public CreateSessionResponse createSession(
            @RequestBody CreateSessionRequest request,
            Authentication auth
    ) {
        Long userId = (Long) auth.getPrincipal();

        log.info("[API] Create session : userId={}, categoryId={}",
                userId, request.categoryId());

        Long sessionId = waitingRequestService.enqueue(
                new CounselRequestDto(
                        userId,
                        request.categoryId(),
                        request.domainId()
                )
        );

        return new CreateSessionResponse(sessionId, "WAITING");
    }


    // ============================================
    // 2. 나의 세션 조회 (유저 or 상담사)
    // ============================================
    @GetMapping("/me")
    public SessionInfoResponse getMySession(Authentication auth) {

        Long id = (Long) auth.getPrincipal();
        log.info("[API] Get my session : id={}", id);

        return chatSessionService.getSessionOfUserOrCounselor(id);
    }


    // ============================================
    // 3. 상담사가 진행중 세션 확인
    // ============================================
    @GetMapping("/active")
    public ResponseEntity<SessionInfoResponse> getActiveSession(
            Authentication auth
    ) {
        Long counselorId = (Long) auth.getPrincipal();
        log.info("[API] Get active session : counselorId={}", counselorId);

        SessionInfoResponse res =
                chatSessionService.getActiveSession(counselorId);

        return ResponseEntity.ok(res);
    }


    // ============================================
    // 4. 상담사가 세션 수락
    // ============================================
    @PatchMapping("/{sessionId}/accept")
    public ResponseEntity<String> acceptSession(
            @PathVariable Long sessionId,
            Authentication auth
    ) {
        Long counselorId = (Long) auth.getPrincipal();
        log.info("[API] Accept session : sessionId={}, counselorId={}",
                sessionId, counselorId);

        chatSessionService.acceptSession(sessionId, counselorId);

        return ResponseEntity.ok("IN_PROGRESS");
    }


    // ============================================
    // 5. 세션 취소 (유저/상담사)
    // ============================================
    @PatchMapping("/{sessionId}/cancel")
    public ResponseEntity<String> cancelSession(
            @PathVariable Long sessionId,
            @RequestBody CancelSessionRequest request,
            Authentication auth
    ) {
        Long actorId = (Long) auth.getPrincipal();

        log.info("[API] Cancel session : sessionId={}, actorId={}, reason={}",
                sessionId, actorId, request.reason());

        chatSessionService.cancelSession(
                sessionId,
                actorId,
                request.reason()
        );

        return ResponseEntity.ok("CANCELLED");
    }

    // ============================================
// 6. 세션 종료 (END)
// ============================================
    @PatchMapping("/{sessionId}/end")
    public ResponseEntity<String> endSession(
            @PathVariable Long sessionId,
            @RequestBody EndSessionRequest request,
            Authentication auth
    ) {
        Long actorId = (Long) auth.getPrincipal();

        log.info("[API] End session : sessionId={}, actorId={}, reason={}",
                sessionId, actorId, request.reason());

        chatSessionService.endSession(
                sessionId,
                actorId,
                request.reason()
        );

        return ResponseEntity.ok("ENDED");
    }
  
}
