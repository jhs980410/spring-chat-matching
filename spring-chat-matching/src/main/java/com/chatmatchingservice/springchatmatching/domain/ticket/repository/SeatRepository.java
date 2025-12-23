package com.chatmatchingservice.springchatmatching.domain.ticket.repository;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findBySectionId(Long sectionId);

    List<Seat> findBySectionIdIn(List<Long> sectionIds);
}
