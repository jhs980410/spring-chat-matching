package com.chatmatchingservice.springchatmatching.domain.ticket.entity;

import com.chatmatchingservice.springchatmatching.domain.event.entity.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "venue_section",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_venue_section_event_code",
                        columnNames = {"event_id", "code"}
                )
        }
)
public class VenueSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false, length = 200)
    private String venue;

    @Column(nullable = false, length = 50)
    private String code;     // 115, FLOOR_A

    @Column(nullable = false, length = 100)
    private String name;     // 115구역

    @Column(nullable = false, length = 20)
    private String grade;    // VIP / R / S

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** 🔥 섹션 가격 = 연결된 티켓 가격 */
    public Long getPrice() {
        return ticket.getPrice();
    }

}
