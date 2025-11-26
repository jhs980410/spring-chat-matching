package com.chatmatchingservice.springchatmatching.domain.chat.service.message;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatMessage;
import com.chatmatchingservice.springchatmatching.domain.chat.repository.ChatMessageRepository;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
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
            // -------------------------
            // 1) 기본 유효성 검사
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
            // 2) 메시지 엔티티 생성
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

            // -------------------------
            // 3) DB 저장
            // -------------------------
            messageRepository.save(entity);

            log.info("[Handler][MESSAGE] 메시지 저장 완료: sessionId={}, senderId={}",
                    msg.getSessionId(), msg.getSenderId());

        } catch (CustomException e) {
            // 이미 정의된 CustomException 그대로 전달
            log.error("[Handler][MESSAGE] CustomException 발생: code={}, msg={}",
                    e.getErrorCode().getCode(), e.getMessage());
            throw e;

        } catch (Exception e) {
            // 예상 외 오류: 서버 내부 오류
            log.error("[Handler][MESSAGE] 처리 중 예상치 못한 예외 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
