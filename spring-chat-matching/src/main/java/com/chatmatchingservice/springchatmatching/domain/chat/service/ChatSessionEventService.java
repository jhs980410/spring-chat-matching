package com.chatmatchingservice.springchatmatching.domain.chat.service;

import com.chatmatchingservice.springchatmatching.domain.chat.service.message.MessageHandler;
import com.chatmatchingservice.springchatmatching.domain.chat.websocket.MessageFactory;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import com.chatmatchingservice.springchatmatching.infra.redis.WSMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * REST API → WebSocket 이벤트 전송 전담
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionEventService {

    private final MessageFactory messageFactory;

    // =====================================================
    // 공통 디스패처 (WSMessage → Handler)
    // =====================================================
    private void dispatch(WSMessage message) {

        if (message == null || message.getType() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            MessageHandler handler = messageFactory.getHandler(message);
            handler.handle(message);

        } catch (CustomException e) {  // 이미 ErrorCode 존재하는 경우
            throw e;

        } catch (Exception e) {
            log.error("[EventService] 메시지 처리 중 오류: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    // =====================================================
    // ASSIGNED
    // =====================================================
    public void sendAssigned(Long sessionId, Long counselorId) {

        if (sessionId == null || counselorId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        WSMessage message = new WSMessage(
                "ASSIGNED",
                sessionId.toString(),
                "SYSTEM",
                counselorId,
                "상담사가 배정되었습니다.",
                Instant.now().toEpochMilli()
        );

        dispatch(message);
        log.info("[Event] ASSIGNED sent: sessionId={}, counselorId={}", sessionId, counselorId);
    }


    // =====================================================
    // ACCEPT
    // =====================================================
    public void sendAccept(Long sessionId, Long counselorId) {

        if (sessionId == null || counselorId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        WSMessage message = new WSMessage(
                "ACCEPT",
                sessionId.toString(),
                "COUNSELOR",
                counselorId,
                "상담사가 상담을 수락했습니다.",
                Instant.now().toEpochMilli()
        );

        dispatch(message);
        log.info("[Event] ACCEPT sent: sessionId={}, counselorId={}", sessionId, counselorId);
    }


    // =====================================================
    // END
    // =====================================================
    public void sendEnd(Long sessionId, Long counselorId) {

        if (sessionId == null || counselorId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        WSMessage message = new WSMessage(
                "END",
                sessionId.toString(),
                "COUNSELOR",
                counselorId,
                "상담이 종료되었습니다.",
                Instant.now().toEpochMilli()
        );

        dispatch(message);
        log.info("[Event] END sent: sessionId={}, counselorId={}", sessionId, counselorId);
    }


    // =====================================================
    // CANCEL
    // =====================================================
    public void sendCancel(Long sessionId, Long actorId, String actorType) {

        if (sessionId == null || actorId == null || actorType == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String msg = "USER".equals(actorType)
                ? "사용자가 상담을 취소했습니다."
                : "상담사가 상담을 취소했습니다.";

        WSMessage message = new WSMessage(
                "CANCEL",
                sessionId.toString(),
                actorType,
                actorId,
                msg,
                Instant.now().toEpochMilli()
        );

        dispatch(message);
        log.info("[Event] CANCEL sent: sessionId={}, actorId={}, actorType={}",
                sessionId, actorId, actorType);
    }


    // =====================================================
    // SYSTEM
    // =====================================================
    public void sendSystem(Long sessionId, String content) {

        if (sessionId == null || content == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        WSMessage message = new WSMessage(
                "SYSTEM",
                sessionId.toString(),
                "SYSTEM",
                0L,
                content,
                Instant.now().toEpochMilli()
        );

        dispatch(message);
        log.info("[Event] SYSTEM sent: sessionId={}, msg={}", sessionId, content);
    }
}
