package com.chatmatchingservice.hqadmin.domain.publish.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat", catalog = "chatmatching") // 운영 DB 스키마 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private VenueSection section;

    @Column(name = "row_label")
    private String rowLabel;    // 행 (예: A열)

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;     // 좌석 번호 (예: 1)

    @Column(name = "is_reserved", nullable = false)
    private boolean isReserved; // 예약 여부 (기본값 false)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public static Seat create(VenueSection section, String rowLabel, int seatNumber) {
        Seat s = new Seat();
        s.section = section;
        s.rowLabel = rowLabel;
        s.seatNumber = seatNumber;
        s.isReserved = false; // 초기 발행 시에는 모두 미예약 상태
        s.createdAt = LocalDateTime.now();
        return s;
    }
}