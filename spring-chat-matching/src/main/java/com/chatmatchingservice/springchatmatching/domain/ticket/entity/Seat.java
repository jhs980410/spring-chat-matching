package com.chatmatchingservice.springchatmatching.domain.ticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "seat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat",
                        columnNames = {"section_id", "row_label", "seat_number"}
                )
        }
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private VenueSection section;

    @Column(name = "row_label", length = 10)
    private String rowLabel;   // A열

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;    // 좌석 번호

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 🔥 추가: 좌석 예약 상태 필드
    private boolean isReserved = false;

    // 🔥 추가: 상태 체크 및 변경 메서드
    public boolean isReserved() {
        return this.isReserved;
    }

    public void reserve() {
        if (this.isReserved) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }
        this.isReserved = true;
    }

    /** 🔥 좌석 가격 = 섹션에 연결된 티켓 가격 */
    public Long getPrice() {
        return section.getTicket().getPrice();
    }
}
