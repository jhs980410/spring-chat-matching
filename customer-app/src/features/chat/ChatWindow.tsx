import { ScrollArea, Text } from "@mantine/core";
import { useEffect, useRef } from "react";
import "./ChatWindow.css";

export interface ChatMessage {
  messageId: number;
  senderType: "USER" | "COUNSELOR";
  senderId: number;
  message: string;
  timestamp: number;
}

export default function ChatWindow({ messages }: { messages: ChatMessage[] }) {
  const viewportRef = useRef<HTMLDivElement>(null);

  // 🔥 새로운 메시지가 들어오면 자동 스크롤
  useEffect(() => {
    if (viewportRef.current) {
      viewportRef.current.scrollTop = viewportRef.current.scrollHeight;
    }
  }, [messages]);

  return (
    <ScrollArea h={400} scrollbarSize={6} viewportRef={viewportRef}>
      <div className="chat-window">
        {messages.map((msg) => {
          const isUser = msg.senderType === "USER";

          // timestamp → HH:mm 변환
          const timeString = msg.timestamp
            ? new Date(msg.timestamp).toLocaleTimeString("ko-KR", {
                hour: "2-digit",
                minute: "2-digit",
              })
            : "";

          return (
            <div
              key={msg.messageId}
              className={`msg-row ${isUser ? "me" : "other"}`}
            >
              <div
                className={`msg-bubble ${
                  isUser ? "me-bubble" : "other-bubble"
                }`}
              >
                {msg.message}
              </div>

              <Text size="xs" c="dimmed" mt={2}>
                {timeString}
              </Text>
            </div>
          );
        })}
      </div>
    </ScrollArea>
  );
}
