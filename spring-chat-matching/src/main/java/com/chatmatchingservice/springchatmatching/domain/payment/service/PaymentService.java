package com.chatmatchingservice.springchatmatching.domain.payment.service;

import com.chatmatchingservice.springchatmatching.domain.order.dto.PaymentResponseDto;
import com.chatmatchingservice.springchatmatching.domain.order.entity.Payment;
import com.chatmatchingservice.springchatmatching.domain.order.entity.PaymentMethod;
import com.chatmatchingservice.springchatmatching.domain.order.repository.PaymentRepository;
import com.chatmatchingservice.springchatmatching.domain.order.repository.TicketOrderRepository;
import com.chatmatchingservice.springchatmatching.domain.order.service.SeatLockService;
import com.chatmatchingservice.springchatmatching.domain.payment.dto.TossPaymentFailRequest;
import com.chatmatchingservice.springchatmatching.domain.payment.dto.TossPaymentSuccessRequest;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final TicketOrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final SeatLockService seatLockService;
    private final TossPaymentClient tossPaymentClient;

    /**
     * 결제 승인 (Confirm)
     */
    public PaymentResponseDto confirmPayment(
            Long userId,
            TossPaymentSuccessRequest request
    ) {
        Long orderId = Long.parseLong(request.orderId());

        // 1️⃣ 주문 조회
        TicketOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));

        // 2️⃣ 주문 소유자 검증
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalStateException("주문 소유자가 아닙니다.");
        }

        // 🔥 3️⃣ 결제 가능 상태 검증 (ORDERED만 허용)
        if (order.getStatus() != TicketOrderStatus.ORDERED) {
            throw new IllegalStateException("결제 가능한 주문 상태가 아닙니다.");
        }

        // 4️⃣ 금액 검증
        if (!order.getTotalPrice().equals(request.amount())) {
            throw new IllegalStateException("결제 금액 불일치");
        }

        // 5️⃣ Toss 서버 승인
        tossPaymentClient.confirm(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );

        // 6️⃣ 주문 상태 변경
        order.markPaid();

        // 7️⃣ 결제 기록 생성
        Payment payment = Payment.create(
                order,
                PaymentMethod.CARD,
                request.amount(),
                request.paymentKey()
        );
        payment.markPaid();
        paymentRepository.save(payment);

        // 8️⃣ Redis 좌석 락 해제
        seatLockService.unlockSeats(
                userId,
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
                userId,
                order.getEvent().getId()
        );

        // ❗ 상태는 CANCEL이 아니라 PENDING 유지 (또는 FAILED 컬럼 도입)
        // 필요하면 로그만 남김
    }
}
