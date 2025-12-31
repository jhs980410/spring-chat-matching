import { useParams } from "react-router-dom";
import { Card, Stack, Title, Box } from "@mantine/core";
import { useEffect, useRef, useState } from "react";
import { notifications } from "@mantine/notifications";

import ChatInput from "./ChatInput";
import ChatWindow from "./ChatWindow";
import type { ChatMessage } from "./ChatWindow";
import { useWS } from "../../api/providers/useWS";
import { useAuthStore } from "../../stores/authStore";
import api from "../../api/axios";

// ===============================
// 타입 정의 (백엔드 WS 메시지 규격)
// ===============================
type WSMessage = {
  sessionId: string;
  role: "USER" | "COUNSELOR";
  senderId: number;
  message: string;
  timestamp: number;
};

export default function ChatPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const { send, subscribe, connected } = useWS();
  const myId = useAuthStore((s) => s.userId);

  const [messages, setMessages] = useState<WSMessage[]>([]);
  const subscribedRef = useRef(false);

  // 1️⃣ 기존 메시지 로드 (REST)
  useEffect(() => {
    if (!sessionId) return;

    // 백엔드 엔드포인트에 맞춰 호출 (예: /sessions/1/detail)
    api.get(`/sessions/${sessionId}/detail`).then((res) => {
      setMessages(res.data.messages ?? []);
    }).catch(() => {
      notifications.show({
        title: "로드 실패",
        message: "이전 대화 내용을 불러올 수 없습니다.",
        color: "red",
      });
    });
  }, [sessionId]);

  // 2️⃣ WS 구독 (실시간 통로)
  useEffect(() => {
    if (!connected || !sessionId) return;
    if (subscribedRef.current) return;

    subscribedRef.current = true;

    const unsubscribe = subscribe(
      `/sub/session/${sessionId}`,
      (payload: WSMessage) => {
        setMessages((prev) => {
          // 중복 수신 방지 (senderId + timestamp 기준)
          if (
            prev.some(
              (m) =>
                m.senderId === payload.senderId &&
                m.timestamp === payload.timestamp
            )
          ) {
            return prev;
          }
          return [...prev, payload];
        });
      }
    );

    return () => {
      subscribedRef.current = false;
      unsubscribe?.();
    };
  }, [connected, sessionId, subscribe]);

  // 3️⃣ 메시지 전송
  const handleSend = (text: string) => {
    if (!sessionId || !connected) {
      notifications.show({
        title: "연결 끊김",
        message: "서버와 연결이 원활하지 않습니다.",
        color: "yellow",
      });
      return;
    }

    send(`/pub/session/${sessionId}`, {
      type: "MESSAGE",
      sessionId,
      message: text,
      timestamp: Date.now(),
    });
  };

  // 4️⃣ UI 컴포넌트용 메시지 변환
  const uiMessages: ChatMessage[] = messages.map((m, idx) => ({
    messageId: `${m.senderId}-${m.timestamp}-${idx}`,
    senderType: m.senderId === myId ? "USER" : "COUNSELOR",
    senderId: m.senderId,
    message: m.message,
    timestamp: m.timestamp,
  }));

  // ===============================
  // UI
  // ===============================
  return (
    <Box p="xl">
      <Card shadow="sm" padding="lg" withBorder style={{ maxWidth: 700 }}>
        <Stack gap="md">
          <Title order={3} fw={700}>실시간 상담 채팅</Title>
          
          {/* 채팅 내역 출력창 */}
          <ChatWindow messages={uiMessages} />
          
          {/* 메시지 입력창 */}
          <ChatInput onSend={handleSend} disabled={!connected} />
        </Stack>
      </Card>
    </Box>
  );
}