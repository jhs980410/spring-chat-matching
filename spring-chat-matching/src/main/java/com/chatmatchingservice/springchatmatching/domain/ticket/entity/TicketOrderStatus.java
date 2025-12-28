package com.chatmatchingservice.springchatmatching.domain.ticket.entity;

public enum TicketOrderStatus {

    PENDING,    // 주문 생성됨 (결제 전)
    ORDERED,    // 좌석/금액 확정 (결제 대기)
    PAID,       // 결제 완료
    CANCELLED,  // 사용자 취소
    REFUNDED    // 환불 완료
}