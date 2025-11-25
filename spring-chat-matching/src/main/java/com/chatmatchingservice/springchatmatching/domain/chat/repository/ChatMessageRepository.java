package com.chatmatchingservice.springchatmatching.domain.chat.repository;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatMessage;
import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 세션별 메시지 조회 (상담 재접속 시 사용)
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);


}
