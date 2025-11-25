package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatMessage;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatMessageRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 일반 채팅 메시지 처리 핸들러
 * type = "MESSAGE"
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSendHandler implements MessageHandler {

    private final ChatMessageRepository messageRepository;

    @Override
    public boolean supports(String type) {
        return "MESSAGE".equalsIgnoreCase(type);
    }

    @Override
    public void handle(WSMessage msg) {
        try {
            ChatMessage entity = ChatMessage.builder()
                    .sessionId(Long.valueOf(msg.getSessionId()))
                    .senderType(msg.getSenderType())
                    .senderId(msg.getSenderId())
                    .message(msg.getMessage())
                    .createdAt(
                            msg.getTimestamp() != null
                                    ? Instant.ofEpochMilli(msg.getTimestamp())
                                    : Instant.now()
                    )
                    .build();

            messageRepository.save(entity);

            log.info("[MessageSendHandler] 메시지 저장 완료: sessionId={}, senderId={}",
                    msg.getSessionId(), msg.getSenderId());

        } catch (Exception e) {
            log.error("[MessageSendHandler] 메시지 처리 실패: {}", e.getMessage(), e);
        }
    }
}
