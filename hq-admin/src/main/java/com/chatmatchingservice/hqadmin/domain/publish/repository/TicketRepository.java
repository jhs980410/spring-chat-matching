package com.chatmatchingservice.hqadmin.domain.publish.repository;

import com.chatmatchingservice.hqadmin.domain.publish.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
