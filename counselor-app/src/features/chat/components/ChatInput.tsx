import { Textarea, Button } from "@mantine/core";
import { useState } from "react";

export default function ChatInput() {
  const [text, setText] = useState("");

  const handleSend = () => {
    if (!text.trim()) return;
    alert("[목업] 전송: " + text);
    setText("");
  };

  return (
    <>
      <Textarea
        placeholder="메시지를 입력하세요"
        value={text}
        onChange={(e) => setText(e.currentTarget.value)}
        mt="md"
      />
      <Button fullWidth mt="sm" radius="md" onClick={handleSend}>
        전송
      </Button>
    </>
  );
}
