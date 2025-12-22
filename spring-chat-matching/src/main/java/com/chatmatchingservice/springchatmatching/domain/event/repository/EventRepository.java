package com.chatmatchingservice.springchatmatching.domain.event.repository;

import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
import com.chatmatchingservice.springchatmatching.domain.event.entity.EventCategory;
import com.chatmatchingservice.springchatmatching.domain.event.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // 메인 노출 (OPEN 중 최신)
    List<Event> findTop5ByStatusOrderByCreatedAtDesc(EventStatus status);

    // 랭킹 (카테고리별)
    List<Event> findTop10ByCategoryAndStatusOrderByCreatedAtDesc(
            EventCategory category,
            EventStatus status
    );

    // 오픈 예정
    List<Event> findTop5ByStartAtAfterOrderByStartAtAsc(LocalDateTime now);
}
