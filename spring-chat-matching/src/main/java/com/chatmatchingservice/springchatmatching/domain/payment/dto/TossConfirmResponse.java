package com.chatmatchingservice.springchatmatching.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossConfirmResponse {

    private String paymentKey;
    private String orderId;
    private String status;   // DONE, CANCELED 등
    private Long totalAmount;
    private Long amount;

    public boolean isDone() {
        return "DONE".equalsIgnoreCase(status);
    }

    public Long getAmount() {
        return amount;
    }
}
