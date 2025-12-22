package com.chatmatchingservice.springchatmatching.domain.ticket.entity;

import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ticket")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, length = 100)
    private String name; // VIP, R석, S석

    @Column(nullable = false)
    private int price;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "remain_quantity", nullable = false)
    private int remainQuantity;
}
