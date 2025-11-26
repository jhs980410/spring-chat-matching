package com.chatmatchingservice.springchatmatching.domain.chat.service.end;

import com.chatmatchingservice.springchatmatching.domain.counselor.service.CounselorEndSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EndSessionFacade {

    private final UserEndSessionService userEndSessionService;
    private final CounselorEndSessionService counselorEndSessionService;

    /**
     * 사용자 종료
     */
    public void endByUser(Long sessionId, Long userId) {
        userEndSessionService.end(sessionId, userId);
    }

    /**
     * 상담사 종료
     */
    public void endByCounselor(Long sessionId, Long counselorId) {
        counselorEndSessionService.end(sessionId, counselorId);
    }
}
