package com.chatmatchingservice.springchatmatching.domain.chat.service.end;

public interface EndSessionService {
    void end(Long sessionId, Long counselorId);
}
