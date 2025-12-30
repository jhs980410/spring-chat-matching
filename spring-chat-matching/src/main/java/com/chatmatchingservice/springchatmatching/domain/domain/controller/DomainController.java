package com.chatmatchingservice.springchatmatching.domain.domain.controller;

import com.chatmatchingservice.springchatmatching.domain.domain.dto.DomainRequest;
import com.chatmatchingservice.springchatmatching.domain.domain.dto.DomainResponse;
import com.chatmatchingservice.springchatmatching.domain.domain.service.DomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Domain",
        description = """
    상담 도메인 관리 API

    - 상담 서비스의 최상위 도메인 관리
    - 카테고리의 상위 기준 데이터
    """
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/domains")
@RequiredArgsConstructor
@Slf4j
public class DomainController {

    private final DomainService domainService;

    // ========================================
    // 도메인 생성
    // ========================================
    @Operation(
            summary = "상담 도메인 생성",
            description = "상담 서비스의 최상위 도메인을 생성하는 API"
    )
    @PostMapping
    public ResponseEntity<DomainResponse> create(@RequestBody DomainRequest req) {
        return ResponseEntity.ok(domainService.create(req));
    }

    // ========================================
    // 도메인 전체 조회
    // ========================================
    @Operation(
            summary = "상담 도메인 조회",
            description = "상담 서비스에서 사용되는 전체 도메인 목록 조회"
    )
    @GetMapping
    public ResponseEntity<List<DomainResponse>> getAll() {
        return ResponseEntity.ok(domainService.findAll());
    }
}
