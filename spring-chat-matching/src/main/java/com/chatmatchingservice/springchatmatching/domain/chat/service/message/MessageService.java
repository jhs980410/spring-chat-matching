package com.chatmatchingservice.springchatmatching.domain.chat.service.message;


import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatMessage;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatMessageRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisKeyManager;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final ChatMessageRepository messageRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 채팅 메시지 처리:
     * 1) DB 저장
     * 2) Redis Pub/Sub으로 WSMessage 전달
     */
    public void handleMessage(WSMessage msg) {
        try {
            Long sessionId = Long.valueOf(msg.getSessionId());

            // 1. DB 저장
            ChatMessage saved = messageRepository.save(
                    ChatMessage.builder()
                            .sessionId(sessionId)
                            .senderType(msg.getSenderType())
                            .senderId(msg.getSenderId())
                            .message(msg.getMessage())
                            .createdAt(Instant.ofEpochMilli(msg.getTimestamp()))
                            .build()
            );

            // 2. Redis Pub/Sub 발행
            String channel = RedisKeyManager.wsChannel(sessionId);
            redisTemplate.convertAndSend(channel, msg);

            log.info("[Message] 채팅 메시지 저장 및 전달 완료. sessionId={}, senderId={}",
                    msg.getSessionId(), msg.getSenderId());

        } catch (Exception e) {
            log.error("[Message] 메시지 처리 중 오류: {}", e.getMessage(), e);
        }
    }
}
