package com.chatmatchingservice.springchatmatching.domain.chat.controller;

import com.chatmatchingservice.springchatmatching.domain.chat.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Slf4j
public class ChatMessageController {

    private final ChatSessionService chatSessionService;

    /**
     * ✨ 메시지 조회 API
     * GET /api/messages/{sessionId}
     */
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
