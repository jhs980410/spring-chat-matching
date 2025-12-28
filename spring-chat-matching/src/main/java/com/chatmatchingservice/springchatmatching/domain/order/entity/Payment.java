package com.chatmatchingservice.springchatmatching.domain.order.entity;

import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "payment",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_payment_key", columnNames = "payment_key")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 주문 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private TicketOrder order;

    /** 결제 수단 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    /** 결제 금액 (서버 확정 금액) */
    @Column(nullable = false)
    private Long amount;

    /** 결제 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    /** PG 거래 ID (선택) */
    @Column(name = "pg_tid", length = 100)
    private String pgTid;

    /** Toss paymentKey (중복 방어 핵심) */
    @Column(name = "payment_key", nullable = false, length = 100)
    private String paymentKey;

    /** 결제 완료 시각 */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /** 생성 시각 */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PaymentStatus.READY;
        }
    }

    /* =========================
       🔥 생성 팩토리
       ========================= */

    /** pgTid 없는 기본 생성 */
    public static Payment create(
            TicketOrder order,
            PaymentMethod method,
            Long amount,
            String paymentKey
    ) {
        Payment payment = new Payment();
        payment.order = order;
        payment.method = method;
        payment.amount = amount;
        payment.paymentKey = paymentKey;
        payment.status = PaymentStatus.READY;
        return payment;
    }

    /** pgTid 포함 생성 */
    public static Payment create(
            TicketOrder order,
            PaymentMethod method,
            Long amount,
            String paymentKey,
            String pgTid
    ) {
        Payment payment = create(order, method, amount, paymentKey);
        payment.pgTid = pgTid;
        return payment;
    }

    /* =========================
       🔥 상태 전이
       ========================= */

    public void markPaid() {
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }
}
