package com.chatmatchingservice.springchatmatching.domain.ticket.entity;

import com.chatmatchingservice.springchatmatching.domain.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "ticket_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TicketOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketOrderStatus status;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TicketOrderItem> items;
    @Column(name = "ordered_at", updatable = false)
    private LocalDateTime orderedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TicketOrderStatus.PENDING;
        }
    }

    public void addItem(TicketOrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }
}
