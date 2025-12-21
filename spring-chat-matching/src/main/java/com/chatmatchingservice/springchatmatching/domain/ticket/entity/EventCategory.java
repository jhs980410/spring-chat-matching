package com.chatmatchingservice.springchatmatching.domain.ticket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // MUSICAL, CONCERT ...

    @Column(nullable = false, length = 100)
    private String name; // 뮤지컬, 콘서트 ...
}
