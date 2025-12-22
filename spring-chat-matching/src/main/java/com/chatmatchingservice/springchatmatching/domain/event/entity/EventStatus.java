package com.chatmatchingservice.springchatmatching.domain.event.entity;

public enum EventStatus {
    OPEN,        // 예매 가능
    SOLD_OUT,    // 매진
    CLOSED       // 종료
}
