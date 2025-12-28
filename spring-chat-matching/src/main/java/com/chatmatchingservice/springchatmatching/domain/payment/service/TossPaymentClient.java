package com.chatmatchingservice.springchatmatching.domain.payment.service;

import com.chatmatchingservice.springchatmatching.domain.payment.dto.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    @Value("${toss.secret-key}")
    private String secretKey;

    private static final String CONFIRM_URL =
            "https://api.tosspayments.com/v1/payments/confirm";

    private final RestTemplate restTemplate = new RestTemplate();

    public TossConfirmResponse confirm(
            String paymentKey,
            String orderId,
            Long amount
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, "");

        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<TossConfirmResponse> response =
                restTemplate.postForEntity(
                        CONFIRM_URL,
                        request,
                        TossConfirmResponse.class
                );

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null) {
            throw new IllegalStateException("토스 결제 승인 실패");
        }

        return response.getBody();
    }
}
