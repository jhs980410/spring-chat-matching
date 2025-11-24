package domain.counselor.controller;
import domain.counselor.dto.CounselRequestDto;
import domain.counselor.service.WaitingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counsel")
@RequiredArgsConstructor
public class CounselRequestController {

    private final WaitingRequestService waitingRequestService;

    @PostMapping("/request")
    public ResponseEntity<String> request(@RequestBody CounselRequestDto dto) {
        // 세션 ID 생성은 UUID, DB insert 등 구현 선택
        String sessionId = waitingRequestService.enqueue(dto);
        return ResponseEntity.ok(sessionId);
    }
}