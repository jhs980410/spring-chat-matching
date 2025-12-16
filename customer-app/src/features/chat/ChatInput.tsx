// features/chat/ChatInput.tsx
import { useState } from "react";
import { TextInput, Button, Group } from "@mantine/core";

type Props = {
  onSend: (text: string) => void;
  disabled?: boolean;
};

export default function ChatInput({ onSend, disabled }: Props) {
  const [text, setText] = useState("");

  const handleSend = () => {
    if (!text.trim()) return;
    onSend(text);
    setText("");
  };

  return (
    <Group mt="md">
      <TextInput
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder={disabled ? "연결 중입니다..." : "메시지를 입력하세요"}
        onKeyDown={(e) => {
          if (e.key === "Enter") {
            e.preventDefault();
            handleSend();
          }
        }}
        style={{ flex: 1 }}
        disabled={disabled}
      />

      <Button onClick={handleSend} disabled={disabled || !text.trim()}>
        전송
      </Button>
    </Group>
  );
}
