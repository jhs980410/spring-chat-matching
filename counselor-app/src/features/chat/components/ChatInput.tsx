import { useState } from "react";
import { TextInput, Button, Group } from "@mantine/core";
import { useWS } from "../../providers/useWS";
import { useAuthStore } from "../../../stores/authStore";
import type { ChatMessage } from "./ChatWindow";

interface Props {
  sessionId: number;
  onNewMessage?: (updater: (prev: ChatMessage[]) => ChatMessage[]) => void;
}

export default function ChatInput({ sessionId, onNewMessage }: Props) {
  const ws = useWS();
  const counselorId = useAuthStore((s) => s.counselorId);
  const [text, setText] = useState("");

  const sendMessage = () => {
    if (!text.trim()) return;

    if (!ws) {
      console.warn("[WS] Not connected. message not sent.");
      return;
    }

    const payload: ChatMessage = {
      messageId: Date.now(),
      senderType: "COUNSELOR",
      senderId: counselorId!,
      message: text,
      timestamp: Date.now(),
    };

    // =============================
    // 서버에 메시지 전송
    // =============================
    try {
      ws.send(`/pub/session/${sessionId}`, {}, JSON.stringify(payload));
    } catch (e) {
      console.error("[WS] SEND ERROR:", e);
    }

    // =============================
    // UI에도 즉시 반영
    // =============================
    if (onNewMessage) {
      onNewMessage((prev) => [...prev, payload]);
    }

    setText("");
  };

  return (
    <Group mt="md">
      <TextInput
        placeholder="메시지를 입력하세요"
        value={text}
        onChange={(e) => setText(e.target.value)}
        style={{ flex: 1 }}
        onKeyDown={(e) => e.key === "Enter" && sendMessage()} // ⭐ Enter 전송 추가
      />
      <Button onClick={sendMessage}>전송</Button>
    </Group>
  );
}
