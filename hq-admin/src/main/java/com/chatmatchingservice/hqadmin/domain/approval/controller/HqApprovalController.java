package com.chatmatchingservice.hqadmin.domain.approval.controller;

import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalListResponse;
import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalRequest;
import com.chatmatchingservice.hqadmin.domain.approval.dto.ApprovalResponse;
import com.chatmatchingservice.hqadmin.domain.approval.service.HqApprovalService;
import com.chatmatchingservice.hqadmin.domain.draft.dto.SalesContractDraftRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 추가된 API: 승인 대기 중인 판매 계약 목록 조회
     */
    @Operation(summary = "승인 대기 계약 목록 조회", description = "상태가 REQUESTED인 모든 판매 계약을 최신순으로 가져옵니다.")
    @GetMapping("/contracts/pending")
    public ResponseEntity<List<SalesContractDraftRecord>> getPendingContracts(
            @RequestHeader("X-ADMIN-ID") Long adminId
    ) {
        return ResponseEntity.ok(approvalService.getPendingContracts());
    }


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

    /**
     * 추가된 API: 승인 대기 중인 공연 초안 목록 조회
     * 엔드포인트: GET /api/hq/approvals/events/pending
     */
    @Operation(summary = "승인 대기 공연 목록 조회")
    @GetMapping("/events/pending")
    public ResponseEntity<List<ApprovalListResponse>> getPendingEvents(
            @RequestHeader("X-ADMIN-ID") Long adminId
    ) {
        // 서비스에서 DraftStatus.REQUESTED 인 EventDraftEntity 목록을 조회하여 반환
        return ResponseEntity.ok(approvalService.getRequestedDrafts());
    }
    //(목록 조회 API)
    @Operation(summary = "발행 대기(승인 완료) 공연 목록 조회")
    @GetMapping("/events/approved")
    public ResponseEntity<List<ApprovalListResponse>> getApprovedEvents(
            @RequestHeader("X-ADMIN-ID") Long adminId
    ) {
        return ResponseEntity.ok(approvalService.getApprovedDrafts());
    }
}