package com.chatmatchingservice.springchatmatching.domain.chat.dto;

public record MessageItem(
        Long id,
        String senderType,
        String senderName,
        String message,
        String createdAt
) {}