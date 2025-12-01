import { ScrollArea, Text } from "@mantine/core";
import "./ChatWindow.css";

export default function ChatWindow({ messages }: any) {
  return (
    <ScrollArea h={400} scrollbarSize={6}>
      <div className="chat-window">
        {messages.map((msg: any) => (
          <div
            key={msg.id}
            className={`msg-row ${msg.sender_type === "COUNSELOR" ? "me" : "other"}`}
          >
            <div
              className={`msg-bubble ${
                msg.sender_type === "COUNSELOR" ? "me-bubble" : "other-bubble"
              }`}
            >
              {msg.message}
            </div>

            <Text size="xs" c="dimmed" mt={2}>
              {msg.created_at.substring(11, 16)}
            </Text>
          </div>
        ))}
      </div>
    </ScrollArea>
  );
}
