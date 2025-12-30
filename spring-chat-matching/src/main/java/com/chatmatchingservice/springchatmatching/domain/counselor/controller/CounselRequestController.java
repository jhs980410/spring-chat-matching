package com.chatmatchingservice.springchatmatching.domain.counselor.controller;
import com.chatmatchingservice.springchatmatching.domain.counselor.dto.CounselRequestDto;
import com.chatmatchingservice.springchatmatching.domain.counselor.service.WaitingRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Counsel Request",
        description = """
    상담 요청 API

    - 상담사가 상담을 요청하면 대기열에 등록
    - 매칭 서비스의 진입 지점
    - 실시간 상담 트래픽의 시작점
    """
)
@RestController
@RequestMapping("/api/counsel")
@RequiredArgsConstructor
public class CounselRequestController {

    private final WaitingRequestService waitingRequestService;

    @Operation(
            summary = "상담 요청",
            description = """
    상담사가 상담 요청을 대기열에 등록하는 API

    - DB에 WAITING 상태의 상담 세션 생성
    - Redis 대기열에 세션 ID 등록
    - 동일 사용자 WAITING 요청 중복 방지
    - 대기열 등록 후 매칭 시도 트리거

    ※ 부하 테스트 핵심 트래픽 API
    """
    )
    @PostMapping("/request")
    public ResponseEntity<Long> request(@RequestBody CounselRequestDto dto) {
        // 세션 ID 생성은 UUID, DB insert 등 구현 선택
        long sessionId = waitingRequestService.enqueue(dto);
        return ResponseEntity.ok(sessionId);
    }
}