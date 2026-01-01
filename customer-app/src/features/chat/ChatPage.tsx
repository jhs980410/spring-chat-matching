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

    api.get(`/sessions/${sessionId}/detail`)
      .then((res) => {
        setMessages(res.data.messages ?? []);
      })
      .catch(() => {
        notifications.show({
          title: "로드 실패",
          message: "이전 대화 내용을 불러올 수 없습니다.",
          color: "red",
        });
      });
  }, [sessionId]);

  // 2️⃣ WS 구독 (실시간 통로)
  useEffect(() => {
    // 세션 정보가 없거나 이미 구독 중이면 중단
    if (!sessionId || subscribedRef.current) return;
    
    // 연결 상태 확인 (connected가 true가 될 때까지 기다림)
    if (!connected) return;

    console.log(`[ChatPage] 구독 시작: /sub/session/${sessionId}`);

    const unsubscribe = subscribe(
      `/sub/session/${sessionId}`,
      (payload: WSMessage) => {
        setMessages((prev) => {
          // 중복 수신 방지
          const isDuplicate = prev.some(
            (m) => m.senderId === payload.senderId && m.timestamp === payload.timestamp
          );
          if (isDuplicate) return prev;
          return [...prev, payload];
        });
      }
    );

    // ✅ 이미지의 경고 해결: 함수 존재 여부가 아니라 구독 성공 시 Ref 업데이트
    subscribedRef.current = true;

    return () => {
      if (unsubscribe) {
        unsubscribe();
        subscribedRef.current = false;
        console.log(`[ChatPage] 구독 해제: /sub/session/${sessionId}`);
      }
    };
  }, [connected, sessionId, subscribe]);

  // 3️⃣ 메시지 전송
  const handleSend = (text: string) => {
    if (!sessionId) return;

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
          
          <ChatWindow messages={uiMessages} />
          
          {/* ✅ 핵심: 연결 상태(connected)가 늦더라도 메시지가 수신되었다면(length > 0) 입력창 활성화 */}
          <ChatInput 
            onSend={handleSend} 
            disabled={!connected && messages.length === 0} 
          />
        </Stack>
      </Card>
    </Box>
  );
}