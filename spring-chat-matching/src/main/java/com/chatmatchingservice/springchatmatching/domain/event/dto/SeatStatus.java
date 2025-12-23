package com.chatmatchingservice.springchatmatching.domain.event.dto;

public enum SeatStatus {
    AVAILABLE,   // 선택 가능
    SELECTED,    // 프론트 선택 상태 (서버 비저장)
    LOCKED,      // Redis 선점됨 (타 유저)
    SOLD         // 결제 완료
}

