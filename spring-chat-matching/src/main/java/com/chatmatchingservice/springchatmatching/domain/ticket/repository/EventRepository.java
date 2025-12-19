package com.chatmatchingservice.springchatmatching.domain.ticket.repository;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Event;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.EventCategory;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // 메인 노출 (HOT / NEW)
    List<Event> findTop5ByStatusOrderByCreatedAtDesc(EventStatus status);

    // 랭킹 (카테고리별)
    List<Event> findTop10ByCategoryAndStatusOrderByCreatedAtDesc(
            EventCategory category,
            EventStatus status
    );

    // 오픈 예정
    List<Event> findTop5ByStartAtAfterOrderByStartAtAsc(LocalDateTime now);

}
