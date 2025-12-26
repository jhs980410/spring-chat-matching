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

    public PaymentResponseDto confirmPayment(
            Long userId,
            TossPaymentSuccessRequest request
    ) {
        // 1 ️order 조회
        TicketOrder order = orderRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));

        // 2️ 금액 검증 (위조 방지)
        if (!order.getTotalPrice().equals(request.amount())) {
            throw new IllegalStateException("결제 금액 불일치");
        }

        // 3️ Toss 서버 승인 (중요)
        tossPaymentClient.confirm(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );

        // 4️⃣ DB 상태 변경
        order.markPaid();

        Payment payment = Payment.create(
                order,
                PaymentMethod.CARD,
                request.amount(),
                request.paymentKey()
        );
        payment.markPaid();

        paymentRepository.save(payment);

        // 5️⃣ Redis 좌석 락 해제
        seatLockService.unlockSeats(userId, order.getEvent().getId());

        return new PaymentResponseDto(
                payment.getId(),
                order.getId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getPaidAt()
        );
    }

    public void failPayment(
            Long userId,
            TossPaymentFailRequest request
    ) {

        TicketOrder order =
                orderRepository.findByOrderId(request.orderId())
                        .orElse(null);

        if (order != null) {
            seatLockService.unlockSeats(userId, order.getEvent().getId());
        }
    }
}
