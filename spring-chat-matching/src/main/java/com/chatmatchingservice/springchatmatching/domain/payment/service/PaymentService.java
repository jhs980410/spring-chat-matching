package com.chatmatchingservice.springchatmatching.domain.payment.service;

import com.chatmatchingservice.springchatmatching.domain.order.dto.PaymentResponseDto;
import com.chatmatchingservice.springchatmatching.domain.order.entity.Payment;
import com.chatmatchingservice.springchatmatching.domain.order.entity.PaymentMethod;
import com.chatmatchingservice.springchatmatching.domain.order.repository.PaymentRepository;
import com.chatmatchingservice.springchatmatching.domain.order.repository.TicketOrderRepository;
import com.chatmatchingservice.springchatmatching.domain.order.service.SeatLockService;
import com.chatmatchingservice.springchatmatching.domain.payment.dto.TossConfirmResponse;
import com.chatmatchingservice.springchatmatching.domain.payment.dto.TossPaymentFailRequest;
import com.chatmatchingservice.springchatmatching.domain.payment.dto.TossPaymentSuccessRequest;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final TicketOrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final SeatLockService seatLockService;
    private final TossPaymentClient tossPaymentClient;

    /**
     * 결제 승인 (Confirm)
     */
    @Transactional
    public PaymentResponseDto confirmPayment(
            Long userId,
            TossPaymentSuccessRequest request
    ) {
        // 0️⃣ Toss orderId → 서버 orderId 복원
        String rawOrderId = request.orderId(); // 예: ORD-000015
        if (!rawOrderId.startsWith("ORD-")) {
            throw new IllegalArgumentException("잘못된 orderId");
        }
        Long orderId = Long.parseLong(rawOrderId.substring(4));

        // 1️⃣ 주문 조회
        TicketOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));

        // 2️⃣ 주문 소유자 검증
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalStateException("주문 소유자가 아닙니다.");
        }

        // 3️⃣ 중복 confirm 방어 (상태)
        if (order.getStatus() == TicketOrderStatus.PAID) {
            throw new IllegalStateException("이미 결제 완료된 주문입니다.");
        }

        // 4️⃣ 중복 confirm 방어 (paymentKey)
        if (paymentRepository.existsByPaymentKey(request.paymentKey())) {
            throw new IllegalStateException("이미 처리된 결제입니다.");
        }

        // 5️⃣ Toss 서버 승인
        TossConfirmResponse toss = tossPaymentClient.confirm(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );

        if (!toss.isDone()) {
            throw new IllegalStateException("결제 승인 실패");
        }

        // 6️⃣ 서버 기준 금액 재계산 (예시)
        Long serverAmount = order.getTotalPrice(); // ← 좌석 기준 계산
        Long paidAmount = toss.getTotalAmount();

        log.info("ORDER totalPrice = {}", order.getTotalPrice());
        log.info("PAID totalAmount = {}", paidAmount);
        if (paidAmount == null) {
            throw new IllegalStateException("Toss 결제 금액 누락");
        }

        if (serverAmount != paidAmount.intValue()) {
            throw new IllegalStateException("결제 금액 불일치");
        }

        // 7️⃣ 주문 PAID 처리
        order.markPaid(serverAmount);

        // 8️⃣ 결제 기록 저장
        Payment payment = Payment.create(
                order,
                PaymentMethod.CARD,
                paidAmount,
                request.paymentKey()
        );
        payment.markPaid();
        paymentRepository.save(payment);

        // 9️⃣ Redis 좌석 락 해제
        seatLockService.unlockSeats(
                order.getId(),
                order.getEvent().getId()
        );

        return new PaymentResponseDto(
                payment.getId(),
                order.getId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getPaidAt()
        );
    }

    /**
     * 결제 실패 처리
     */
    public void failPayment(
            Long userId,
            TossPaymentFailRequest request
    ) {

        Long orderId = Long.parseLong(request.orderId());

        TicketOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));

        // 주문 소유자 검증
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalStateException("주문 소유자가 아닙니다.");
        }

        // 좌석 락 해제
        seatLockService.unlockSeats(
                order.getId(),
                order.getEvent().getId()
        );

        // ❗ 상태는 CANCEL이 아니라 PENDING 유지 (또는 FAILED 컬럼 도입)
        // 필요하면 로그만 남김
    }
}
