package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 상담사 배정(ASSIGNED) 메시지 처리 핸들러
 */
@Component
@Slf4j
public class AssignedHandler implements MessageHandler {

    @Override
    public boolean supports(String type) {
        return "ASSIGNED".equals(type);
    }

    @Override
    public void handle(WSMessage message) {
        log.info("[Handler][ASSIGNED] 상담사 배정 이벤트 처리: {}", message);
        // TODO: 세션 상태 캐싱, 알림, 모니터링 등 여기에 확장 가능
    }
}
