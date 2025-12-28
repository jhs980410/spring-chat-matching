package com.chatmatchingservice.springchatmatching.domain.order.repository;

import com.chatmatchingservice.springchatmatching.domain.order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    boolean existsByPaymentKey(String paymentKey);
}
