package domain.chat.controller;

import domain.counselor.dto.CounselRequestDto;
import domain.counselor.dto.CounselorStatusUpdateRequest;
import domain.counselor.service.CounselorStatusService;
import domain.counselor.service.WaitingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MatchingController {

    private final WaitingRequestService waitingRequestService;
    private final CounselorStatusService counselorStatusService;

    /**
     * 고객 상담 요청 → 대기열 push + 세션 생성
     */
    @PostMapping("/chat/request")
    public Long requestChat(@RequestBody CounselRequestDto dto) {
        return waitingRequestService.enqueue(dto);
    }


    /**
     * 상담사 상태 업데이트 → READY면 매칭 시도
     */
    @PostMapping("/counselor/status")
    public void updateStatus(@RequestBody CounselorStatusUpdateRequest req) {
        counselorStatusService.updateStatus(req.getCounselorId(), req);
    }
}

