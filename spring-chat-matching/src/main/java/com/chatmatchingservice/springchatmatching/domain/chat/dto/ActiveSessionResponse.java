package com.chatmatchingservice.springchatmatching.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActiveSessionResponse {
    private Long sessionId;
}