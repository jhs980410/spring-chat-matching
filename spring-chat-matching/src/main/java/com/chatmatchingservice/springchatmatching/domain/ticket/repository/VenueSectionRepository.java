package com.chatmatchingservice.springchatmatching.domain.ticket.repository;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.VenueSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueSectionRepository extends JpaRepository<VenueSection, Long> {

    List<VenueSection> findByEventId(Long eventId);
}
