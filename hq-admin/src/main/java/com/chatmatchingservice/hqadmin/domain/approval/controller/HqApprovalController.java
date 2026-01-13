package com.chatmatchingservice.hqadmin.domain.approval.controller;

import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalRequest;
import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalResponse;
import com.chatmatchingservice.hqadmin.domain.approval.service.HqApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "HQ Approval",
        description = "본사(HQ) 관리자용 승인/반려 통합 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hq/approvals") // 공통 경로
public class HqApprovalController {

    private final HqApprovalService approvalService;

    /**
     * 1. 판매 계약(Contract) 승인 API
     * 타겟 테이블: ticket_manager.sales_contract_draft
     */
    @Operation(summary = "판매 계약 승인", description = "매니저가 신청한 판매 계약을 승인합니다.")
    @PostMapping("/contracts/{draftId}/approve")
    public ResponseEntity<ApprovalResponse> approveContract(
            @RequestHeader("X-ADMIN-ID") Long adminId,
            @PathVariable Long draftId
    ) {
        // 서비스의 계약 승인 전용 로직 호출
        return ResponseEntity.ok(
                approvalService.approveContract(draftId, adminId)
        );
    }

    /**
     * 2. 공연(Event) 승인 API
     * 타겟 테이블: ticket_manager.event_draft
     */
    @Operation(summary = "공연 Draft 승인", description = "계약 승인 후 등록된 공연 초안을 승인합니다.")
    @PostMapping("/events/{draftId}/approve")
    public ResponseEntity<ApprovalResponse> approveEvent(
            @RequestHeader("X-ADMIN-ID") Long adminId,
            @PathVariable Long draftId
    ) {
        // 서비스의 공연 승인 전용 로직 호출
        return ResponseEntity.ok(
                approvalService.approve(draftId, adminId)
        );
    }

    /**
     * 3. 공연(Event) 반려 API
     */
    @Operation(summary = "공연 Draft 반려")
    @PostMapping("/events/{draftId}/reject")
    public ResponseEntity<ApprovalResponse> rejectEvent(
            @RequestHeader("X-ADMIN-ID") Long adminId,
            @PathVariable Long draftId,
            @RequestBody ApprovalRequest request
    ) {
        return ResponseEntity.ok(
                approvalService.reject(draftId, adminId, request)
        );
    }
}