package com.chatmatchingservice.springchatmatching.domain.chat.controller;

import com.chatmatchingservice.springchatmatching.domain.chat.service.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Chat Message",
        description = """
    상담 메시지 조회 API

    - 상담 세션 내 메시지 목록 조회
    - 사용자 / 상담사 / 관리자 접근 가능
    - 세션 접근 권한 검증 포함
    """
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Slf4j
public class ChatMessageController {

    private final ChatSessionService chatSessionService;

    // ============================================
    // 메시지 조회
    // ============================================
    @Operation(
            summary = "상담 메시지 조회",
            description = """
        특정 상담 세션의 메시지 목록을 조회하는 API

        - 세션 접근 권한 검증 후 메시지 반환
        - 생성 시간 기준 오름차순 정렬
        """
    )
    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getMessages(
            @PathVariable Long sessionId,
            Authentication auth
    ) {
        Long actorId = (Long) auth.getPrincipal();

        log.info("[API] Get messages: sessionId={}, actorId={}", sessionId, actorId);

        return ResponseEntity.ok(
                chatSessionService.getMessages(sessionId, actorId)
        );
    }
}
