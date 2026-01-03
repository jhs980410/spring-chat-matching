package com.chatmatchingservice.springchatmatching.domain.chat.controller;

import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselRequestDto;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselorStatusUpdateRequest;
import com.chatmatchingservice.springchatmatching.domain.counselor.service.CounselorStatusService;
import com.chatmatchingservice.springchatmatching.domain.counselor.service.WaitingRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Matching",
        description = """
    상담 매칭 관련 API

    - 상담사 상태 변경 처리
    - 매칭 로직 트리거용 진입점
    """
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MatchingController {

    private final WaitingRequestService waitingRequestService;
    private final CounselorStatusService counselorStatusService;

    @Operation(
            summary = "상담 상태 요청",
            description = """
    고객의 상담 요청을 WAITING 상태로 등록하는 API

    - DB에 WAITING 상태의 상담 세션 생성
    - Redis 대기열에 세션 ID 등록
    - 동일 고객의 중복 WAITING 요청 방지
    - 등록 직후 매칭 가능 여부 확인을 위해 매칭 시도 트리거

    ※ 부하 테스트 핵심 트래픽 API
    """
    )
    @PostMapping("/chat/request")
    public Long requestChat(@RequestBody CounselRequestDto dto) {
        return waitingRequestService.enqueue(dto);
    }


    @Operation(
            summary = "상담사 상태 변경",
            description = """
    상담사의 상태를 변경하는 API

    - READY 상태 전환 시 매칭 로직이 트리거됨
    - 상담사 가용성 관리용 API
    """
    )
    @PostMapping("/counselor/status")
    public void updateStatus(@RequestBody CounselorStatusUpdateRequest req) {
        counselorStatusService.updateStatus(req.getCounselorId(), req);
    }
}

