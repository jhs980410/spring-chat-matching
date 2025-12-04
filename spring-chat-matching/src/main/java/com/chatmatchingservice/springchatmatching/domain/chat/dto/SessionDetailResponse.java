package com.chatmatchingservice.springchatmatching.domain.chat.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record SessionDetailResponse(

        // --- 세션 메인 정보 ---
        Long sessionId,
        String status,

        Long userId,
        String userName,
        String userEmail,

        Long counselorId,
        String counselorName,

        String domainName,
        String categoryName,

        String requestedAt,
        String assignedAt,
        String startedAt,
        String endedAt,
        Long durationSec,

        // --- 메시지 목록 ---
        List<MessageItem> messages,

        // --- After-call 정보 ---
        Integer satisfactionScore,
        Integer afterCallSec,
        String feedback,
        String afterCallEndedAt
) {

    // ---------------------------------------------------------------------
    // 🔥 정적 팩토리 메서드 (핵심)
    // ---------------------------------------------------------------------
    public static SessionDetailResponse of(
            Object[] s,                // 세션 메인 정보
            List<Object[]> m,          // 메시지 목록
            Object[] a                 // After-call 정보
    ) {

        // === 메시지 변환 ===
        List<MessageItem> messageList = new ArrayList<>();
        if (m != null) {
            for (Object[] row : m) {
                messageList.add(new MessageItem(
                        toLong(row[0]),
                        toStringVal(row[1]),
                        toLong(row[2]),
                        toStringVal(row[3]),
                        toStringVal(row[4]),
                        toMillis(row[5])
                ));
            }
        }

        // === after-call ===
        Integer satisfactionScore = a != null ? toInteger(a[0]) : null;
        Integer afterCallSec = a != null ? toInteger(a[1]) : null;
        String feedback = a != null ? toStringVal(a[2]) : null;
        String afterCallEndedAt = a != null ? toStringVal(a[3]) : null;

        // === 세션 메인 ===
        return new SessionDetailResponse(
                toLong(s[0]),
                toStringVal(s[1]),

                toLong(s[2]),
                toStringVal(s[3]),
                toStringVal(s[4]),

                toLong(s[5]),
                toStringVal(s[6]),

                toStringVal(s[7]),
                toStringVal(s[8]),

                toStringVal(s[9]),
                toStringVal(s[10]),
                toStringVal(s[11]),
                toStringVal(s[12]),
                toLong(s[13]),

                messageList,

                satisfactionScore,
                afterCallSec,
                feedback,
                afterCallEndedAt
        );
    }

    // ---------------------------------------------------------------------
    // 🔧 유틸 변환 함수들
    // ---------------------------------------------------------------------

    private static Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Long l) return l;
        if (o instanceof Integer i) return i.longValue();
        if (o instanceof Number n) return n.longValue();
        return Long.valueOf(o.toString());
    }

    private static Integer toInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Integer i) return i;
        if (o instanceof Number n) return n.intValue();
        return Integer.valueOf(o.toString());
    }

    private static String toStringVal(Object o) {
        return o == null ? null : o.toString();
    }

    private static Long toMillis(Object o) {
        if (o == null) return null;
        if (o instanceof Instant i) return i.toEpochMilli();
        return null;
    }

    // ---------------------------------------------------------------------
    // 🔥 메시지 DTO 내부 클래스
    // ---------------------------------------------------------------------
    public record MessageItem(
            Long id,
            String senderType,
            Long senderId,
            String senderName,
            String message,
            Long createdAtMillis
    ) {}
}
