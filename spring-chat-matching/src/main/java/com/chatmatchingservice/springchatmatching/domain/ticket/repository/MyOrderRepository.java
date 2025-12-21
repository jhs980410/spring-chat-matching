package com.chatmatchingservice.springchatmatching.domain.ticket.repository;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder; // 실제 엔티티 클래스
import com.chatmatchingservice.springchatmatching.domain.ticket.dto.MyOrderRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

// 1. JpaRepository 상속 필수! (엔티티 타입과 ID 타입을 명시)
public interface MyOrderRepository extends JpaRepository<TicketOrder, Long> {

    @Query("""
    SELECT new com.chatmatchingservice.springchatmatching.domain.ticket.dto.MyOrderRow(
        o.id,
        o.status,
        o.createdAt,
        o.totalPrice,

        e.id,
        e.title,
        e.thumbnail,
        e.venue,
        e.startAt,

        t.name,
        i.quantity,
        i.unitPrice
    )
    FROM TicketOrder o
    JOIN o.event e
    JOIN o.items i
    JOIN i.ticket t
    WHERE o.user.id = :userId
    ORDER BY o.createdAt DESC
""")
    List<MyOrderRow> findMyOrders(@Param("userId") Long userId);


}