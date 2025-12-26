package com.chatmatchingservice.springchatmatching.domain.ticket.repository;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findBySectionId(Long sectionId);

    List<Seat> findBySectionIdIn(List<Long> sectionIds);
    @Query("""
        select s
        from Seat s
        join fetch s.section sec
        join fetch sec.ticket t
        where sec.event.id = :eventId
    """)
    List<Seat> findAllByEventId(@Param("eventId") Long eventId);
}
