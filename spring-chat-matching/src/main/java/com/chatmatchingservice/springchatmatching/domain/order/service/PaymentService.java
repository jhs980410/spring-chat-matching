package com.chatmatchingservice.springchatmatching.domain.order.service;

import com.chatmatchingservice.springchatmatching.domain.order.dto.PaymentRequestDto;
import com.chatmatchingservice.springchatmatching.domain.order.dto.PaymentResponseDto;
import com.chatmatchingservice.springchatmatching.domain.order.entity.Payment;
import com.chatmatchingservice.springchatmatching.domain.order.repository.PaymentRepository;
import com.chatmatchingservice.springchatmatching.domain.order.repository.TicketOrderRepository;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.TicketOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TicketOrderRepository orderRepository;
    private final SeatLockService seatLockService;

    public PaymentResponseDto pay(
            Long userId,
            PaymentRequestDto request
    ) {

        TicketOrder order = orderRepository.findById(request.orderId())
                .orElseThrow();

        Payment payment = Payment.create(order, request.method());
        payment.markPaid();

        paymentRepository.save(payment);

        // 결제 성공 → Redis 락 해제
        seatLockService.unlockSeats(userId, order.getEvent().getId());

        return new PaymentResponseDto(
                payment.getId(),
                order.getId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getPaidAt()
        );
    }
}
