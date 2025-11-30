package com.chatmatchingservice.springchatmatching.domain.category.repository;

import com.chatmatchingservice.springchatmatching.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDomainId(Long domainId);
}
