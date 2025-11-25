package com.chatmatchingservice.springchatmatching.domain.chat.dto;

public record CancelSessionRequest(
        String reason   // 사용자가 취소하는 사유 (선택적)
) {}