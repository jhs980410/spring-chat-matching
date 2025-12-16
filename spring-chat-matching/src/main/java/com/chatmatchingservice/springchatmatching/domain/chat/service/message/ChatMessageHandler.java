package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageHandler implements MessageHandler {

    private final MessageService messageService;

    @Override
    public void handle(WSMessage message) {
        log.info("[WS][Handler] MESSAGE 처리: {}", message);
        log.warn("🚨🚨🚨 ChatMessageHandler.handle() 호출됨 🚨🚨🚨");
        messageService.handleMessage(message);
    }

    @Override
    public boolean supports(String type) {
        return "MESSAGE".equals(type);
    }
}
