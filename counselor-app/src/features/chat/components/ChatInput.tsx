// ChatInput.tsx
import { useState } from "react";
import { TextInput, Button, Group } from "@mantine/core";
import { useWS } from "../../providers/useWS";
import { useAuthStore } from "../../../stores/authStore";
import type { ChatMessage } from "./ChatWindow";

interface Props {
  sessionId: number;
  onNewMessage: (updater: (prev: ChatMessage[]) => ChatMessage[]) => void;
}

export default function ChatInput({ sessionId, onNewMessage }: Props) {
  const { client, connected } = useWS();
  const counselorId = useAuthStore((s) => s.counselorId);
  const [text, setText] = useState("");

  const sendMessage = () => {
    if (!text.trim()) return;
    if (!connected || !client) {
      console.warn("[WS] not connected, message blocked");
      return;
    }

    // 🔹 서버로 전송할 payload
    const payload = {
      type: "MESSAGE",
      sessionId,
      message: text,
      timestamp: Date.now(),
    };

    try {
      client.send(
        `/pub/session/${sessionId}`,
        {},
        JSON.stringify(payload)
      );
    } catch (e) {
      console.error("[WS] send error", e);
      return;
    }

    // 🔹 optimistic UI 업데이트
    onNewMessage((prev) => [
      ...prev,
      {
        messageId: Date.now(), // 임시 ID
        senderType: "COUNSELOR",
        senderId: counselorId ?? 0,
        message: text,
        timestamp: payload.timestamp,
      },
    ]);

    setText("");
  };

  return (
    <Group mt="md">
      <TextInput
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder={
          connected ? "메시지를 입력하세요" : "연결 중입니다..."
        }
        onKeyDown={(e) => {
          if (e.key === "Enter") {
            e.preventDefault();
            sendMessage();
          }
        }}
        style={{ flex: 1 }}
        disabled={!connected}
      />

      <Button onClick={sendMessage} disabled={!connected || !text.trim()}>
        전송
      </Button>
    </Group>
  );
}
