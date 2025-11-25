package com.chatmatchingservice.springchatmatching.domain.chat.repository;

import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionJpaRepository extends JpaRepository<ChatSession, Long> {
}
