// features/chat/ChatMessageList.tsx

import { Box, Text } from "@mantine/core";

type Message = {
  sessionId: string;
  role: "USER" | "COUNSELOR";
  senderId: number;
  message: string;
  timestamp: number;
};

type Props = {
  messages: Message[];
  myId: number;
};

export default function ChatMessageList({ messages, myId }: Props) {
  return (
    <Box
      style={{
        height: 400,
        overflowY: "auto",
        marginTop: 16,
        marginBottom: 16,
      }}
    >
      {messages.map((msg, idx) => {
        const isMine = msg.senderId === myId;

        return (
          <Box
            key={`${msg.timestamp}-${idx}`}
            style={{
              textAlign: isMine ? "right" : "left",
              marginBottom: 8,
            }}
          >
            <Text
              style={{
                display: "inline-block",
                padding: "8px 12px",
                borderRadius: 8,
                backgroundColor: isMine ? "#e7f5ff" : "#f1f3f5",
                maxWidth: "80%",
              }}
            >
              {msg.message}
            </Text>
          </Box>
        );
      })}
    </Box>
  );
}
