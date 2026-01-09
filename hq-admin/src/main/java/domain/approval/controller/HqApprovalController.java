package domain.approval.controller;

import domain.approval.dto.ApprovalRequest;
import domain.approval.dto.ApprovalResponse;
import domain.approval.service.HqApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "HQ Approval",
        description = """
        본사(HQ) 승인/반려 API

        - REQUESTED 상태 Draft만 처리 가능
        - 승인/반려 이력은 event_approval 테이블에 기록
        """
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hq/approvals")
public class HqApprovalController {

    private final HqApprovalService approvalService;

    // ========================================
    // 승인
    // ========================================
    @Operation(summary = "Draft 승인")
    @PostMapping("/{draftId}/approve")
    public ResponseEntity<ApprovalResponse> approve(
            @RequestHeader("X-ADMIN-ID") Long adminId,
            @PathVariable Long draftId
    ) {
        return ResponseEntity.ok(
                approvalService.approve(draftId, adminId)
        );
    }

    // ========================================
    // 반려
    // ========================================
    @Operation(summary = "Draft 반려")
    @PostMapping("/{draftId}/reject")
    public ResponseEntity<ApprovalResponse> reject(
            @RequestHeader("X-ADMIN-ID") Long adminId,
            @PathVariable Long draftId,
            @RequestBody ApprovalRequest request
    ) {
        return ResponseEntity.ok(
                approvalService.reject(draftId, adminId, request)
        );
    }
}
