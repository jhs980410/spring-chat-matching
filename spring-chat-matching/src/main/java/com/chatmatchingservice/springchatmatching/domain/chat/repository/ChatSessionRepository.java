    package com.chatmatchingservice.springchatmatching.domain.chat.repository;
    
    import com.chatmatchingservice.springchatmatching.domain.chat.entity.ChatSession;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    
    
    import java.util.List;
    import java.util.Optional;
    
    public interface ChatSessionRepository {
    
        ChatSession createWaitingSession(Long userId, Long categoryId,Long domainId);
    
        void assignCounselor(Long sessionId, long counselorId);
        void endSession(Long sessionId,String endReason);
         void endSession(Long sessionId);
        Optional<ChatSession> findById(Long sessionId);
        Optional<ChatSession> findActiveSessionByUser(Long userId);
        Optional<ChatSession> findActiveSessionByCounselor(Long counselorId);
    
        void markSessionStarted(Long sessionId);

        // ==========================================
        // 🔥 추가되는 “조회 전용” 메서드 3개
        // ==========================================

        /**
         * 세션 단건 상세 (유저/상담사/도메인/카테고리 JOIN)
         */
        Object[] findSessionDetail(Long sessionId);


        /**
         * 메시지 목록 (메시지 + 발신자 이름 JOIN)
         */
        List<Object[]> findMessages(Long sessionId);


        /**
         * After-call 로그 조회
         */
        Object[] findAfterCall(Long sessionId);

        List<Object[]> findHistoryOfCounselor(Long counselorId);
        List<Object[]> findAllHistory();

    }
