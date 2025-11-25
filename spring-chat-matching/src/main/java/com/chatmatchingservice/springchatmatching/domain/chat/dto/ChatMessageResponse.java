package com.chatmatchingservice.springchatmatching.domain.chat.dto;

public record ChatMessageResponse(
        Long messageId,
        String senderType,
        Long senderId,
        String message,
        long timestamp
) {}