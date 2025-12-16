// features/chat/ChatPage.tsx
import { useParams } from "react-router-dom";
import { Card, Stack, Title } from "@mantine/core";
import { useEffect, useRef, useState } from "react";
import { notifications } from "@mantine/notifications";

import ChatInput from "./ChatInput";
import ChatWindow from "./ChatWindow";
import type { ChatMessage } from "./ChatWindow";
import { useWS } from "../../api/providers/useWS";
import { useAuthStore } from "../../stores/authStore";
import api from "../../api/axios";

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

    api.get(`/sessions/${sessionId}/detail`).then((res) => {
      setMessages(res.data.messages ?? []);
    });
  }, [sessionId]);

  // 2️⃣ WS 구독 (🔥 유일한 실시간 통로)
  useEffect(() => {
    if (!connected || !sessionId) return;
    if (subscribedRef.current) return;

    subscribedRef.current = true;

    const unsubscribe = subscribe(
      `/sub/session/${sessionId}`,
      (payload: WSMessage) => {
        setMessages((prev) => {
          // ✅ senderId + timestamp 기준 중복 차단
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

  // 3️⃣ 메시지 전송 (❌ 낙관적 추가 없음)
  const handleSend = (text: string) => {
    if (!sessionId || !connected) {
      notifications.show({
        title: "연결 중",
        message: "서버와 연결 중입니다.",
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

  // 4️⃣ UI 메시지 변환
  const uiMessages: ChatMessage[] = messages.map((m, idx) => ({
    messageId: `${m.senderId}-${m.timestamp}-${idx}`, // UI용 키
    senderType: m.senderId === myId ? "USER" : "COUNSELOR",
    senderId: m.senderId,
    message: m.message,
    timestamp: m.timestamp,
  }));

  return (
    <div style={{ maxWidth: 600, margin: "40px auto" }}>
      <Card shadow="sm" padding="lg">
        <Stack>
          <Title order={3}>상담 채팅</Title>
          <ChatWindow messages={uiMessages} />
          <ChatInput onSend={handleSend} disabled={!connected} />
        </Stack>
      </Card>
    </div>
  );
}
