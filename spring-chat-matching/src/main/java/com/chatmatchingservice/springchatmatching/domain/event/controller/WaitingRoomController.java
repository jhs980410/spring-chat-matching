package com.chatmatchingservice.springchatmatching.domain.event.controller;

import com.chatmatchingservice.springchatmatching.domain.event.service.WaitingRoomService;
import com.chatmatchingservice.springchatmatching.domain.event.service.WaitingRoomService.WaitingStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.security.core.Authentication; // 추가


@RestController
@RequestMapping("/api/waiting-room")
@RequiredArgsConstructor
public class WaitingRoomController {

    private final WaitingRoomService waitingRoomService;

    /**
     * 1. 대기열 진입 API
     */
    @PostMapping("/{eventId}/join")
    public ResponseEntity<WaitingStatusResponse> joinQueue(
            @PathVariable Long eventId,
            Authentication auth // Authentication 객체에서 userId 추출
    ) {
        Long userId = (Long) auth.getPrincipal(); // MyOrderController와 동일한 방식
        return ResponseEntity.ok(waitingRoomService.joinWaitingQueue(eventId, userId));
    }

    /**
     * 2. 상태 조회 API (Polling용)
     */
    @GetMapping("/{eventId}/status")
    public ResponseEntity<WaitingStatusResponse> getStatus(
            @PathVariable Long eventId,
            Authentication auth // 여기서도 동일하게 추출
    ) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(waitingRoomService.checkStatus(eventId, userId));
    }
}
