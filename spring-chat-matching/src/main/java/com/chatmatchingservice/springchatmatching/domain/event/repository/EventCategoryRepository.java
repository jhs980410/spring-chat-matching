package com.chatmatchingservice.springchatmatching.domain.event.repository;

import com.chatmatchingservice.springchatmatching.domain.event.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
    Optional<EventCategory> findByCode(String code);
}
