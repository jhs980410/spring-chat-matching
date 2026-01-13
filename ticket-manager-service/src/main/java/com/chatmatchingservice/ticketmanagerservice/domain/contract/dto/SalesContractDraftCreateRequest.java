package com.chatmatchingservice.ticketmanagerservice.domain.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SalesContractDraftCreateRequest(
        @Schema(description = "파트너 초안 ID", example = "1001")
        Long partnerDraftId,

        @Schema(description = "도메인 ID", example = "1")
        Long domainId,

        @Schema(description = "사업자명", example = "(주)티켓매니아")
        String businessName,

        @Schema(description = "사업자번호", example = "123-45-67890")
        String businessNumber,

        @Schema(description = "대표자명", example = "홍길동")
        String ceoName,

        @Schema(description = "담당자 이메일", example = "manager@test.com")
        String contactEmail,

        @Schema(description = "담당자 전화번호", example = "010-1234-5678")
        String contactPhone,

        @Schema(description = "정산용 이메일", example = "settle@test.com")
        String settlementEmail,

        @Schema(description = "매출 보고용 이메일", example = "report@test.com")
        String salesReportEmail,

        @Schema(description = "세금계산서 이메일", example = "tax@test.com")
        String taxEmail,

        @Schema(description = "발권 방식 (ONLINE, ON_SITE, DELIVERY)", example = "ONLINE")
        String issueMethod
) {}