package com.chatmatchingservice.springchatmatching.domain.counselor.controller;

import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorStatusUpdateRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.service.CounselorStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counselors")
@RequiredArgsConstructor
public class CounselorStatusController {

    private final CounselorStatusService counselorStatusService;

    @PostMapping("/{counselorId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable long counselorId,
            @RequestBody CounselorStatusUpdateRequest request
    ) {
        counselorStatusService.updateStatus(counselorId, request);
        return ResponseEntity.ok().build();
    }
}
