package com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.controller;

import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.eventDraft.EventDraftCreateRequest;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.eventDraft.EventDraftDetailResponse;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.eventDraft.EventDraftResponse;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.dto.ticketDraft.TicketDraftCreateRequest;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.entity.DraftStatus;
import com.chatmatchingservice.ticketmanagerservice.domain.ticket_request.service.TicketRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Ticket Draft (Manager)",
        description = """
        티켓 매니저용 공연/티켓 Draft 관리 API

        - 판매 계약(SalesContractDraft)에 종속된 공연/티켓 Draft 생성
        - 승인 요청
        - Draft 목록 및 상세 조회
        - 승인 전까지 운영 DB에는 반영되지 않음
        """
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/drafts")
public class TicketRequestController {

    private final TicketRequestService ticketRequestService;

    // ========================================
    // 1) Draft 생성
    // ========================================
    @Operation(
            summary = "공연/티켓 Draft 생성",
            description = """
            티켓 매니저가 판매 계약(SalesContractDraft)에 종속된
            공연(Event)과 티켓(Ticket) 정보를 Draft 형태로 생성합니다.

            - 생성 직후 상태는 DRAFT
            - 승인 요청 전까지 외부 노출 불가
            - 최소 1개 이상의 TicketDraft 필요
            """
    )
    @PostMapping
    public ResponseEntity<Long> createDraft(
            @RequestHeader("X-MANAGER-ID") Long managerId,
            @RequestBody CreateDraftRequest request
    ) {
        Long draftId = ticketRequestService.createDraft(
                managerId,
                request.salesContractDraftId(),
                request.event(),
                request.tickets()
        );
        return ResponseEntity.ok(draftId);
    }

    /**
     * Draft 생성 요청용 Wrapper DTO
     */
    public record CreateDraftRequest(
            Long salesContractDraftId,                 // 🔥 필수
            EventDraftCreateRequest event,
            List<TicketDraftCreateRequest> tickets
    ) {}

    // ========================================
    // 2) Draft 승인 요청
    // ========================================
    @Operation(
            summary = "Draft 승인 요청",
            description = """
            Draft를 승인 요청 상태(REQUESTED)로 변경합니다.

            - DRAFT 상태에서만 요청 가능
            - 요청 이후 수정 불가
            - 요청은 본사(HQ Admin) 승인 대상이 됩니다.
            """
    )
    @PostMapping("/{id}/request")
    public ResponseEntity<Void> requestApproval(
            @RequestHeader("X-MANAGER-ID") Long managerId,
            @PathVariable Long id
    ) {
        ticketRequestService.requestApproval(id, managerId);
        return ResponseEntity.ok().build();
    }

    // ========================================
    // 3) Draft 목록 조회
    // ========================================
    @Operation(
            summary = "Draft 목록 조회",
            description = """
            티켓 매니저가 생성한 Draft 목록을 조회합니다.

            - status 파라미터 미지정 시 전체 조회
            - status 지정 시 상태별 조회 가능 (DRAFT / REQUESTED)
            """
    )
    @GetMapping
    public ResponseEntity<List<EventDraftResponse>> getDrafts(
            @RequestHeader("X-MANAGER-ID") Long managerId,
            @RequestParam(required = false) DraftStatus status
    ) {
        return ResponseEntity.ok(
                ticketRequestService.getDrafts(managerId, status)
        );
    }

    // ========================================
    // 4) Draft 상세 조회
    // ========================================
    @Operation(
            summary = "Draft 상세 조회",
            description = """
            특정 Draft의 상세 정보를 조회합니다.

            - EventDraft 정보 + TicketDraft 목록 반환
            - 판매 계약에 종속된 Draft
            - 본인 소유 Draft만 조회 가능
            """
    )
    @GetMapping("/{id}")
    public ResponseEntity<EventDraftDetailResponse> getDraftDetail(
            @RequestHeader("X-MANAGER-ID") Long managerId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ticketRequestService.getDraftDetail(id, managerId)
        );
    }
}
