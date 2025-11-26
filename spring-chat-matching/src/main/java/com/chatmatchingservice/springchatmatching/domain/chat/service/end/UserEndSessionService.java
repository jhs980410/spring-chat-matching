package com.chatmatchingservice.springchatmatching.domain.chat.service.end;

import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 사용자 종료(USER) 시나리오 템플릿 구현
 */
@Service
@Slf4j
public class UserEndSessionService extends EndSessionTemplate implements EndSessionService {

    public UserEndSessionService(ChatSessionRepository sessionRepository,
                                 RedisTemplate<String, Object> redisTemplate) {
        super(sessionRepository, redisTemplate);
    }

    /**
     * 세션 종료 로그 저장
     * (추후 DB 로그 테이블로 확장 가능)
     */
    @Override
    protected void saveLog(Long sessionId, Long counselorId) {
        log.info("[EndSession][USER] counsel_log 저장: sessionId={}, counselorId={}",
                sessionId, counselorId);
    }

    /**
     * EndSessionService 인터페이스 구현
     */
    @Override
    public void end(Long sessionId, Long counselorId) {
        super.endSession(sessionId, counselorId);
    }
}
