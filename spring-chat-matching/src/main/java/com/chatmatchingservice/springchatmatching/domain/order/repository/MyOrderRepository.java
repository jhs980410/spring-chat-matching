package com.chatmatchingservice.springchatmatching.domain.order.repository;

import com.chatmatchingservice.springchatmatching.domain.order.dto.MyOrderRow;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyOrderRepository extends JpaRepository<TicketOrder, Long> {

    /* =========================
     * 1️⃣ 예매 목록 (Row)
     * ========================= */
    @Query("""
    select new com.chatmatchingservice.springchatmatching.domain.order.dto.MyOrderRow(
        o.id,
        o.status,
        o.orderedAt,
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
    from TicketOrder o
    join o.event e
    join o.items i
    join i.seat s
    join s.section sec
    join sec.ticket t
    where o.user.id = :userId
    order by o.orderedAt desc
""")
    List<MyOrderRow> findMyOrders(@Param("userId") Long userId);


    /* =========================
     * 2️⃣ 주문 상세 (Row 여러 줄)
     * ========================= */
    @Query("""
    select new com.chatmatchingservice.springchatmatching.domain.order.dto.MyOrderRow(
        o.id,
        o.status,
        o.orderedAt,
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
    from TicketOrder o
    join o.event e
    join o.items i
    join i.seat s
    join s.section sec
    join sec.ticket t
    where o.id = :orderId
      and o.user.id = :userId
""")
    List<MyOrderRow> findOrderDetail(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );

    /* =========================
     * 3️⃣ 홈 - 상태별 카운트
     * ========================= */
    long countByUser_Id(Long userId);

    long countByUser_IdAndStatus(Long userId, TicketOrderStatus status);


    /* =========================
     * 4️⃣ 관람 완료 카운트
     * ========================= */
    @Query("""
        select count(o)
        from TicketOrder o
        join o.event e
        where o.user.id = :userId
          and o.status = 'PAID'
          and e.startAt < current_timestamp
    """)
    long countCompleted(@Param("userId") Long userId);
}
