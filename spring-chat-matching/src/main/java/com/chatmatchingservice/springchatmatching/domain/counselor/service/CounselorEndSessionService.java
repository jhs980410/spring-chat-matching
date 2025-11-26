package com.chatmatchingservice.springchatmatching.domain.counselor.service;

import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatSessionRepository;
import com.chatmatchingservice.springchatmatching.domain.chat.service.end.EndSessionService;
import com.chatmatchingservice.springchatmatching.domain.chat.service.end.EndSessionTemplate;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 상담사 종료(COUNSELOR) 시나리오 템플릿 구현
 */
@Service
@Slf4j
public class CounselorEndSessionService extends EndSessionTemplate implements EndSessionService {

    public CounselorEndSessionService(ChatSessionRepository sessionRepository,
                                      RedisRepository redisRepository) {
        super(sessionRepository, redisRepository);
    }

    /**
     * 세션 종료 로그 저장
     * (추후 DB 로그 테이블로 확장 가능)
     */
    @Override
    protected void saveLog(Long sessionId, Long counselorId) {
        log.info("[EndSession][COUNSELOR] counsel_log 저장: sessionId={}, counselorId={}",
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
