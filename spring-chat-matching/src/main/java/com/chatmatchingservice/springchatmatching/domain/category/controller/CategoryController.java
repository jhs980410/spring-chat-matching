package com.chatmatchingservice.springchatmatching.domain.category.controller;

import com.chatmatchingservice.springchatmatching.domain.category.dto.CategoryRequest;
import com.chatmatchingservice.springchatmatching.domain.category.dto.CategoryResponse;
import com.chatmatchingservice.springchatmatching.domain.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Category",
        description = """
    상담 카테고리 관리 API

    - 상담 도메인 하위 카테고리 조회
    - 상담 요청 및 매칭 시 참조되는 기준 데이터
    """
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService service;

    // ========================================
    // 1) 전체 조회 or 도메인별 조회
    // ========================================
    @Operation(
            summary = "상담 카테고리 조회",
            description = """
        상담 카테고리 목록을 조회하는 API

        - domainId 미지정 시 전체 조회
        - domainId 지정 시 해당 도메인 하위 카테고리 조회
        """
    )
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> get(
            @RequestParam(required = false) Long domainId
    ) {
        if (domainId == null) {
            return ResponseEntity.ok(service.findAll());
        }
        return ResponseEntity.ok(service.findByDomain(domainId));
    }

    // ========================================
    // 2) 카테고리 생성 (도메인 하위)
    // ========================================
    @Operation(
            summary = "상담 카테고리 생성",
            description = "특정 상담 도메인 하위에 카테고리를 생성하는 API"
    )
    @PostMapping("/domains/{domainId}")
    public ResponseEntity<CategoryResponse> create(
            @PathVariable Long domainId,
            @RequestBody CategoryRequest req
    ) {
        return ResponseEntity.ok(service.create(domainId, req));
    }
}
