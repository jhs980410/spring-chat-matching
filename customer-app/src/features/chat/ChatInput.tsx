// features/chat/ChatInput.tsx

import { useState } from "react";
import { Group, TextInput, Button } from "@mantine/core";

type Props = {
  onSend: (message: string) => void;
};

export default function ChatInput({ onSend }: Props) {
  const [text, setText] = useState("");

  const handleSend = () => {
    if (!text.trim()) return;
    onSend(text);
    setText("");
  };

  return (
    <Group gap="sm">
      <TextInput
        placeholder="메시지를 입력하세요"
        value={text}
        onChange={(e) => setText(e.target.value)}
        style={{ flex: 1 }}
        onKeyDown={(e) => {
          if (e.key === "Enter") {
            handleSend();
          }
        }}
      />
      <Button onClick={handleSend}>전송</Button>
    </Group>
  );
}
