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

        return new CategoryResponse(c.getId(), c.getCode(), c.getName());
    }

    public List<CategoryResponse> findByDomain(Long domainId) {
        return categoryRepository.findByDomainId(domainId).stream()
                .map(c -> new CategoryResponse(c.getId(), c.getCode(), c.getName()))
                .toList();
    }
}
