// ChatInput.tsx
import { useState } from "react";
import { TextInput, Button, Group } from "@mantine/core";
import { useWS } from "../../providers/useWS";

type Props = {
  sessionId: number;
};

export default function ChatInput({ sessionId }: Props) {
  const { client, connected } = useWS();
  const [text, setText] = useState("");
  const [sending, setSending] = useState(false); // 🔒 중복 SEND 방지

  const sendMessage = () => {
    if (!text.trim()) return;
    if (!connected || !client || sending) return;

    setSending(true);

    try {
      client.send(
        `/pub/session/${sessionId}`,
        {},
        JSON.stringify({
          type: "MESSAGE",
          sessionId,
          message: text,
          // ❌ timestamp 제거 → 서버에서 생성
        })
      );

      setText(""); // 입력창만 초기화
    } catch (e) {
      console.error("[WS] send error", e);
    } finally {
      setSending(false);
    }
  };

  return (
    <Group mt="md">
      <TextInput
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder={connected ? "메시지를 입력하세요" : "연결 중입니다..."}
        onKeyDown={(e) => {
          if (e.key === "Enter") {
            e.preventDefault();
            sendMessage();
          }
        }}
        style={{ flex: 1 }}
        disabled={!connected || sending}
      />

      <Button
        onClick={sendMessage}
        disabled={!connected || !text.trim() || sending}
      >
        전송
      </Button>
    </Group>
  );
}
