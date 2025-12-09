package com.chatmatchingservice.springchatmatching.domain.category.service;

import com.chatmatchingservice.springchatmatching.domain.category.dto.CategoryRequest;
import com.chatmatchingservice.springchatmatching.domain.category.dto.CategoryResponse;
import com.chatmatchingservice.springchatmatching.domain.category.entity.Category;
import com.chatmatchingservice.springchatmatching.domain.category.repository.CategoryRepository;
import com.chatmatchingservice.springchatmatching.domain.domain.entity.Domain;
import com.chatmatchingservice.springchatmatching.domain.domain.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final DomainRepository domainRepository;

    // ======================================================
    // 신규 추가 카테고리 생성 (Domain 포함)
    // ======================================================
    public CategoryResponse create(Long domainId, CategoryRequest req) {

        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new RuntimeException("Domain not found"));

        Category c = categoryRepository.save(
                Category.builder()
                        .domain(domain)
                        .code(req.code())
                        .name(req.name())
                        .build()
        );

        return new CategoryResponse(
                c.getId(),
                c.getCode(),
                c.getName(),
                domain.getId(),
                domain.getName()
        );
    }

    // ======================================================
    // 특정 도메인의 카테고리 조회
    // ======================================================
    public List<CategoryResponse> findByDomain(Long domainId) {
        return categoryRepository.findByDomainId(domainId).stream()
                .map(c -> new CategoryResponse(
                        c.getId(),
                        c.getCode(),
                        c.getName(),
                        c.getDomain().getId(),
                        c.getDomain().getName()
                ))
                .toList();
    }

    // ======================================================
    // 🔥 전체 카테고리 조회 (READY 셀렉션용)
    // ======================================================
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAllWithDomain().stream()
                .map(c -> new CategoryResponse(
                        c.getId(),
                        c.getCode(),
                        c.getName(),
                        c.getDomain().getId(),
                        c.getDomain().getName()
                ))
                .toList();
    }
}
