package com.chatmatchingservice.springchatmatching.domain.category.controller;

import com.chatmatchingservice.springchatmatching.domain.category.dto.CategoryRequest;
import com.chatmatchingservice.springchatmatching.domain.category.dto.CategoryResponse;
import com.chatmatchingservice.springchatmatching.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService service;

    // ========================================
    // 1) 전체 조회 or 도메인별 조회
    // ========================================
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
    @PostMapping("/domains/{domainId}")
    public ResponseEntity<CategoryResponse> create(
            @PathVariable Long domainId,
            @RequestBody CategoryRequest req
    ) {
        return ResponseEntity.ok(service.create(domainId, req));
    }
}
