package com.chatmatchingservice.ticketmanagerservice.domain.contract.controller;

import com.chatmatchingservice.ticketmanagerservice.domain.contract.dto.SalesContractDraftCreateRequest;
import com.chatmatchingservice.ticketmanagerservice.domain.contract.dto.SalesContractDraftResponse;
import com.chatmatchingservice.ticketmanagerservice.domain.contract.service.SalesContractDraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Sales Contract Draft (Manager)",
        description = """
        티켓 매니저용 판매 계약 Draft API

        - 판매 계약 Draft 생성
        - 승인 요청
        - 계약 Draft 목록 조회
        - 계약 승인 후에만 공연/티켓 등록 가능
        """
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/contracts")
public class SalesContractDraftController {

    private final SalesContractDraftService contractService;

    // ========================================
    // 1) 계약 Draft 생성
    // ========================================
    @Operation(
            summary = "판매 계약 Draft 생성",
            description = """
            티켓 매니저가 판매 계약 Draft를 생성합니다.

            - 생성 직후 상태는 DRAFT
            - 승인 전까지 공연/티켓 등록 불가
            """
    )
    @PostMapping
    public ResponseEntity<Long> createContract(
            @RequestHeader("X-MANAGER-ID") Long managerId,
            @RequestBody SalesContractDraftCreateRequest request
    ) {
        Long contractId = contractService.create(managerId, request);
        return ResponseEntity.ok(contractId);
    }

    // ========================================
    // 2) 계약 승인 요청
    // ========================================
    @Operation(
            summary = "계약 승인 요청",
            description = """
            판매 계약 Draft를 승인 요청 상태로 변경합니다.

            - DRAFT 상태에서만 요청 가능
            - 본사(HQ Admin)의 승인 대상이 됩니다.
            """
    )
    @PostMapping("/{id}/request")
    public ResponseEntity<Void> requestApproval(
            @RequestHeader("X-MANAGER-ID") Long managerId,
            @PathVariable Long id
    ) {
        contractService.request(id, managerId);
        return ResponseEntity.ok().build();
    }

    // ========================================
    // 3) 계약 Draft 목록 조회
    // ========================================
    @Operation(
            summary = "내 계약 Draft 목록 조회",
            description = """
            티켓 매니저 본인이 생성한 계약 Draft 목록을 조회합니다.
            """
    )
    @GetMapping
    public ResponseEntity<List<SalesContractDraftResponse>> getMyContracts(
            @RequestHeader("X-MANAGER-ID") Long managerId
    ) {
        return ResponseEntity.ok(
                contractService.getMyDrafts(managerId)
        );
    }
}
