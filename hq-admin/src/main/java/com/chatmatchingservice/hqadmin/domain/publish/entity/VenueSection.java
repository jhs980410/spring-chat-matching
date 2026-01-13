package com.chatmatchingservice.hqadmin.domain.publish.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Entity
@Table(name = "venue_section", catalog = "chatmatching")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VenueSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💡 Long eventId 대신 객체 참조로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // 💡 Long ticketId 대신 객체 참조로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    private String venue;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String grade;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 💡 서비스 로직과 일치하도록 수정된 create 메서드
    public static VenueSection create(
            Event event,
            Ticket ticket,
            String venue,
            String code,
            String name,
            String grade
    ) {
        VenueSection vs = new VenueSection();
        vs.event = event;   // 이제 ID가 아니라 객체 자체를 할당
        vs.ticket = ticket; // 객체 자체를 할당
        vs.venue = venue;
        vs.code = code;
        vs.name = name;
        vs.grade = grade;
        vs.createdAt = LocalDateTime.now();
        return vs;
    }
}