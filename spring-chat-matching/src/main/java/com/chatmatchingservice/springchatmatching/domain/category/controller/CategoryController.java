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
@RequestMapping("/api/domains/{domainId}/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @PathVariable Long domainId,
            @RequestBody CategoryRequest req
    ) {
        return ResponseEntity.ok(service.create(domainId, req));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getByDomain(
            @PathVariable Long domainId
    ) {
        return ResponseEntity.ok(service.findByDomain(domainId));
    }
}
