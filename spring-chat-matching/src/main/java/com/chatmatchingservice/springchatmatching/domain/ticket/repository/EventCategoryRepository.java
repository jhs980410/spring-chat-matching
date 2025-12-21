package com.chatmatchingservice.springchatmatching.domain.ticket.repository;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
    Optional<EventCategory> findByCode(String code);
}
