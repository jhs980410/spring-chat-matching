package com.chatmatchingservice.springchatmatching.domain.order.repository;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TicketOrderItemRepository extends JpaRepository<TicketOrderItem, Long> {

    List<TicketOrderItem> findByOrderId(Long orderId);

    @Query("""
    select distinct s.id
    from TicketOrderItem i
    join i.order o
    join i.seat s
    where o.event.id = :eventId
      and o.status = 'PAID'
""")
    Set<Long> findSoldSeatIds(Long eventId);

}
