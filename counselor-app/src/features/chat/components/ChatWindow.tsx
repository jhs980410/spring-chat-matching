import { ScrollArea, Text } from "@mantine/core";
import "./ChatWindow.css";

interface ChatMessage {
  messageId: number;
  senderType: "USER" | "COUNSELOR";
  senderId: number;
  message: string;
  timestamp: number; // 서버 JSON 기준
}

export default function ChatWindow({ messages }: { messages: ChatMessage[] }) {
  return (
    <ScrollArea h={400} scrollbarSize={6}>
      <div className="chat-window">
        {messages.map((msg) => {
          const isCounselor = msg.senderType === "COUNSELOR";

          // timestamp → HH:mm
          const timeString = msg.timestamp
            ? new Date(msg.timestamp).toISOString().substring(11, 16)
            : "";

          return (
            <div
              key={msg.messageId}
              className={`msg-row ${isCounselor ? "me" : "other"}`}
            >
              <div
                className={`msg-bubble ${
                  isCounselor ? "me-bubble" : "other-bubble"
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
