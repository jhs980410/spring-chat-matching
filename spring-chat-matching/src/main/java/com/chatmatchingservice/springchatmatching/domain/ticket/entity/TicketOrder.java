package com.chatmatchingservice.springchatmatching.domain.ticket.entity;

import com.chatmatchingservice.springchatmatching.domain.ReserveUser.entity.ReserveUser;
import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
import com.chatmatchingservice.springchatmatching.domain.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "ticket_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_user_id", nullable = true) // 🔥 반드시 true
    private ReserveUser reserveUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketOrderStatus status;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice = 0L;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketOrderItem> items = new ArrayList<>();

    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TicketOrderStatus.PENDING;
        }
    }

    /* =========================
       🔥 핵심 도메인 메서드
       ========================= */

    public static TicketOrder create(
            AppUser user,
            ReserveUser reserveUser,
            Event event
    ) {
        TicketOrder order = new TicketOrder();
        order.user = user;
        order.reserveUser = reserveUser;
        order.event = event;
        order.status = TicketOrderStatus.PENDING;
        return order;
    }

    public static TicketOrder create(AppUser user, Event event) {
        TicketOrder order = new TicketOrder();
        order.user = user;
        order.event = event;
        order.status = TicketOrderStatus.PENDING;
        return order;
    }
    //결제성공시//
    public void assignReserveUser(ReserveUser reserveUser) {
        this.reserveUser = reserveUser;
    }

    /** 🔥 양방향 연관관계 핵심 */
    public void addItem(TicketOrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

    /** 🔥 주문 확정 */
    public void confirmOrder() {
        this.totalPrice = items.stream()
                .mapToLong(TicketOrderItem::getPrice)
                .sum();
        this.status = TicketOrderStatus.ORDERED;
        this.orderedAt = LocalDateTime.now();
    }

    public void markPaid() {
        this.status = TicketOrderStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = TicketOrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public boolean isPaid() {
        return this.status == TicketOrderStatus.PAID;
    }
}
