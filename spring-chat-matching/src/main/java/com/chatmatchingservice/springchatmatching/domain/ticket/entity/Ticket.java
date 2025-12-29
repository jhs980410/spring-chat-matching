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
    private Long price;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "remain_quantity", nullable = false)
    private int remainQuantity;

    /* =========================
       🔥 비즈니스 로직
       ========================= */

    /** 가격 스냅샷 제공 */
    public Long getUnitPrice() {
        return this.price;
    }

    /** 수량 가능 여부 확인 */
    public void validateAvailable(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }

        if (count > 4) {
            throw new IllegalArgumentException("티켓은 최대 4장까지 예매 가능합니다.");
        }

        if (this.remainQuantity < count) {
            throw new IllegalStateException("잔여 티켓 수량이 부족합니다.");
        }
    }

    /** 수량 차감 (결제 직전 or 확정 시점) */
    public void decrease(int count) {
        validateAvailable(count);
        this.remainQuantity -= count;
    }

    /** 수량 복구 (결제 실패 / 취소) */
    public void restore(int count) {
        this.remainQuantity += count;
        if (this.remainQuantity > this.totalQuantity) {
            this.remainQuantity = this.totalQuantity;
        }
    }
}
