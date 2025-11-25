package com.chatmatchingservice.springchatmatching.domain.chat.service.end;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EndSessionFacade {

    private final UserEndSessionService userEndSessionService;

    public void endByUser(Long sessionId, Long userId) {
        userEndSessionService.endSession(sessionId, userId);
    }
}
