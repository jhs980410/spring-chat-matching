package com.chatmatchingservice.springchatmatching.domain.order.repository;



import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, Long> {

    List<TicketOrder> findByUserIdOrderByOrderedAtDesc(Long userId);

    Optional<TicketOrder> findById(Long id);
}