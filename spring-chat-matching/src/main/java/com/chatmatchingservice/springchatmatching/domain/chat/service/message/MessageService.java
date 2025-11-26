package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatMessage;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatMessageRepository;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final ChatMessageRepository messageRepository;
    private final RedisRepository redisRepository;   // 🔥 RedisRepository 기반

    /**
     * 채팅 메시지 처리:
     * 1) WSMessage 유효성 체크
     * 2) DB 저장
     * 3) Redis Pub/Sub 발행
     */
    public void handleMessage(WSMessage msg) {

        try {
            // -------------------------
            // 1) 유효성 검사
            // -------------------------
            if (msg == null) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            if (msg.getSessionId() == null || msg.getSessionId().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            if (msg.getMessage() == null || msg.getMessage().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            Long sessionId;
            try {
                sessionId = Long.valueOf(msg.getSessionId());
            } catch (Exception e) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            // -------------------------
            // 2) DB 저장
            // -------------------------
            ChatMessage entity = ChatMessage.builder()
                    .sessionId(sessionId)
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

            // -------------------------
            // 3) Redis Pub/Sub 발행 (RedisRepository 사용)
            // -------------------------
            try {
                redisRepository.publishToWsChannel(sessionId, msg);   // 🔥 변경됨
            } catch (Exception e) {
                log.error("[MessageService] Redis publish 실패: {}", e.getMessage());
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            log.info("[MessageService] 메시지 저장 및 전달 완료 sessionId={}, senderId={}",
                    msg.getSessionId(), msg.getSenderId());

        } catch (CustomException e) {
            log.error("[MessageService] CustomException: code={}, msg={}",
                    e.getErrorCode().getCode(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("[MessageService] 처리 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
