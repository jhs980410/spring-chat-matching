package com.chatmatchingservice.springchatmatching.domain.chat.dto;

public record AfterCallLog(
        Integer satisfactionScore,
        Integer afterCallSec,
        String feedback,
        String endedAt
) {}
