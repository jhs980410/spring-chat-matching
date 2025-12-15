// features/chat/ChatPage.tsx

import { useParams } from "react-router-dom";
import { Card, Stack, Title } from "@mantine/core";
import { useEffect, useState } from "react";
import { notifications } from "@mantine/notifications";

import ChatInput from "./ChatInput";
import ChatMessageList from "./ChatMessageList";
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

  // ===============================
  // 1. 기존 메시지 로드
  // ===============================
  useEffect(() => {
    if (!sessionId) return;

    api.get(`/sessions/${sessionId}/detail`).then((res) => {
      setMessages(res.data.messages ?? []);
    });
  }, [sessionId]);

  // ===============================
  // 2. WS 구독
  // ===============================
  useEffect(() => {
    if (!connected || !sessionId) return;

    const unsubscribe = subscribe(
      `/sub/session/${sessionId}`,
      (payload: WSMessage) => {
        setMessages((prev) => [...prev, payload]);
      }
    );

    return () => {
      unsubscribe?.();
    };
  }, [connected, sessionId, subscribe]);

  // ===============================
  // 3. 메시지 전송 (🔥 핵심 수정)
  // ===============================
  const handleSend = (text: string) => {
    if (!sessionId) return;

    if (!connected) {
      notifications.show({
        title: "연결 중",
        message: "서버와 연결 중입니다. 잠시만 기다려주세요.",
        color: "yellow",
      });
      return;
    }

    send(`/pub/session/${sessionId}`, {
      sessionId: String(sessionId),
      message: text,
    });

    // 낙관적 업데이트
    setMessages((prev) => [
      ...prev,
      {
        sessionId: String(sessionId),
        role: role === "USER" ? "USER" : "COUNSELOR",
        senderId: myId!,
        message: text,
        timestamp: Date.now(),
      },
    ]);
  };

  return (
    <div style={{ maxWidth: 600, margin: "40px auto" }}>
      <Card shadow="sm" padding="lg">
        <Stack>
          <Title order={3}>상담 채팅</Title>

          <ChatMessageList messages={messages} myId={myId!} />

          <ChatInput onSend={handleSend} />
        </Stack>
      </Card>
    </div>
  );
}
