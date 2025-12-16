// features/chat/ChatPage.tsx
import { useParams } from "react-router-dom";
import { Card, Stack, Title } from "@mantine/core";
import { useEffect, useState } from "react";
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
  const role = useAuthStore((s) => s.role);

  const [messages, setMessages] = useState<WSMessage[]>([]);

  // 1️⃣ 기존 메시지 로드
  useEffect(() => {
    if (!sessionId) return;

    api.get(`/sessions/${sessionId}/detail`).then((res) => {
      setMessages(res.data.messages ?? []);
    });
  }, [sessionId]);

  // 2️⃣ WS 구독
  useEffect(() => {
    if (!connected || !sessionId) return;

    const unsubscribe = subscribe(
      `/sub/session/${sessionId}`,
      (payload: WSMessage) => {
        setMessages((prev) => [...prev, payload]);
      }
    );

    return () => unsubscribe?.();
  }, [connected, sessionId, subscribe]);

  // 3️⃣ 메시지 전송
  const handleSend = (text: string) => {
    if (!sessionId) return;

    if (!connected) {
      notifications.show({
        title: "연결 중",
        message: "서버와 연결 중입니다.",
        color: "yellow",
      });
      return;
    }

    const timestamp = Date.now();

    send(`/pub/session/${sessionId}`, {
      type: "MESSAGE",
      sessionId,
      message: text,
      timestamp,
    });

    // optimistic update
    setMessages((prev) => [
      ...prev,
      {
        sessionId,
        role: role ?? "USER",
        senderId: myId!,
        message: text,
        timestamp,
      },
    ]);
  };

  // 🔥 WSMessage → ChatMessage 변환
  const uiMessages: ChatMessage[] = messages.map((m, idx) => ({
    messageId: m.timestamp ?? idx,
    senderType: m.role,
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
