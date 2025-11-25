package com.chatmatchingservice.springchatmatching.domain.chat.dto;

public record EndSessionRequest(
        String reason // USER / COUNSELOR / TIMEOUT / ADMIN
) {}
