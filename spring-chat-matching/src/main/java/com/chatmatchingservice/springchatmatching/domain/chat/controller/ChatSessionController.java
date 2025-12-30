package com.chatmatchingservice.springchatmatching.domain.chat.controller;

import com.chatmatchingservice.springchatmatching.domain.chat.dto.*;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import com.chatmatchingservice.springchatmatching.domain.chat.service.ChatSessionService;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselRequestDto;
import com.chatmatchingservice.springchatmatching.domain.counselor.service.WaitingRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Chat Session",
        description = """
    상담 세션 관리 API

    - 상담 세션 생성, 조회, 상태 전이 관리
    - 사용자 / 상담사 / 관리자 공통 접근
    - 상담 세션의 전체 생명주기(State Machine) 관리
    """
)
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
    @Operation(
            summary = "상담 세션 생성",
            description = """
    사용자의 상담 요청을 기반으로 상담 세션을 생성하는 API

    - 상담 요청을 WAITING 상태의 세션으로 생성
    - Redis 대기열 등록은 WaitingRequestService를 통해 처리
    - 동일 사용자 중복 WAITING 요청 방지
    - 세션 생명주기의 시작점

    ※ 부하 테스트 핵심 트래픽 API
    """
    )
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
    @Operation(
            summary = "내 진행 중 세션 조회",
            description = """
    현재 로그인 사용자의 진행 중인 상담 세션 조회

    - 사용자 또는 상담사 기준
    - WAITING / IN_PROGRESS 세션만 조회
    """
    )
    @GetMapping("/me")
    public SessionInfoResponse getMySession(Authentication auth) {

        Long id = (Long) auth.getPrincipal();
        log.info("[API] Get my session : id={}", id);

        return chatSessionService.getSessionOfUserOrCounselor(id);
    }


    // ============================================
    // 3. 상담사가 진행중 세션 확인
    // ============================================
    @Operation(
            summary = "상담사 진행 중 세션 조회",
            description = "상담사가 현재 담당 중인 상담 세션 ID 조회"
    )
    @GetMapping("/active")
    public ResponseEntity<ActiveSessionResponse> getActiveSession(Authentication auth) {
        Long counselorId = (Long) auth.getPrincipal();
        Long sessionId = chatSessionService.getActiveSessionId(counselorId);

        return ResponseEntity.ok(
                ActiveSessionResponse.builder()
                        .sessionId(sessionId)
                        .build()
        );
    }



    // ============================================
    // 4. 상담사가 세션 수락
    // ============================================
    @Operation(
            summary = "상담 세션 수락",
            description = """
    상담사가 배정된 상담 세션을 수락하는 API

    - 세션 상태를 IN_PROGRESS로 전환
    - 상담 시작 시점 기록
    - Redis 및 이벤트 동기화

    ※ 상태 전이 핵심 API
    """
    )
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
    @Operation(
            summary = "상담 세션 취소",
            description = """
    진행 중인 상담 세션을 취소하는 API

    - 사용자 또는 상담사 취소 가능
    - 세션 상태를 CANCELLED로 전환
    """
    )
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
    @Operation(
            summary = "상담 세션 종료",
            description = """
    진행 중인 상담 세션을 종료하는 API

    - 사용자 또는 상담사 종료 가능
    - 세션 상태를 ENDED로 전환
    - 상담 종료 이벤트 발행
    """
    )
    @PatchMapping("/{sessionId}/end")
    public ResponseEntity<String> endSession(
            @PathVariable Long sessionId,
            @RequestBody(required = false) EndSessionRequest request,
            Authentication auth
    ) {
        Long actorId = (Long) auth.getPrincipal();

        log.info("[API] End session : sessionId={}, actorId={}, reason={}",
                sessionId, actorId,
                request != null ? request.reason() : null);

        chatSessionService.endSession(
                sessionId,
                actorId,
                request != null ? request.reason() : null
        );

        return ResponseEntity.ok("ENDED");
    }
    // ============================================
// 7. 세션 단건 상세 조회
// ============================================
    @Operation(
            summary = "상담 세션 상세 조회",
            description = "상담 세션 단건 상세 정보 조회"
    )
    @GetMapping("/{sessionId}")
    public SessionInfoResponse getSessionDetail(
            @PathVariable Long sessionId,
            Authentication auth
    ) {
        Long actorId = (Long) auth.getPrincipal();
        log.info("[API] Get session detail : sessionId={}, actorId={}", sessionId, actorId);

        ChatSession session = chatSessionService.getAndValidateSession(sessionId, actorId);

        return chatSessionService.convertToResponse(session);
    }
    @Operation(summary = "상담사 상담 이력 조회")
    @GetMapping("/history/counselor")
    public List<SessionHistoryResponse> getCounselorHistory(Authentication auth) {
        Long counselorId = (Long) auth.getPrincipal();
        log.info("[API] Get counselor history : counselorId={}", counselorId);

        return chatSessionService.getHistoryOfCounselor(counselorId);
    }
    @Operation(summary = "전체 상담 이력 조회 (관리자)")
    @GetMapping("/history")
    public List<SessionHistoryResponse> getAllHistory(Authentication auth) {
        Long adminId = (Long) auth.getPrincipal();
        log.info("[API] Admin gets all history : adminId={}", adminId);

        return chatSessionService.getAllHistory();
    }
    @Operation(summary = "단건 상담 이력 상세")
    @GetMapping("/{sessionId}/detail")
    public SessionDetailResponse getDetail(
            @PathVariable Long sessionId,
            Authentication auth
    ) {
        Long actorId = (Long) auth.getPrincipal();

        System.out.println("actorid는 : "+actorId);
        System.out.println("sessionId는 : "+sessionId);
        return chatSessionService.getSessionDetail(sessionId, actorId);
    }

}
