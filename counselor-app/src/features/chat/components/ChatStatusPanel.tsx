import { Card, Text, Button, Divider } from "@mantine/core";
import { useState } from "react";
import AfterCallForm from "./AfterCallForm";

export default function ChatStatusPanel({ session }: any) {
  // 초기 상태 (mock 기반)
  const initialStatus = session.end_reason
    ? "ENDED"
    : session.started_at
    ? "IN_PROGRESS"
    : "WAITING";

  const [status, setStatus] = useState(initialStatus);

  // 🔹 상담 종료 → AFTER_CALL
  const handleEnd = () => {
    alert("[목업] 상담 종료 처리");
    setStatus("AFTER_CALL");
  };

  // 🔹 후처리 완료 → READY
  const handleAfterCallDone = () => {
    alert("[목업] 후처리 완료 → READY");
    setStatus("READY");
  };

  // 🔹 READY (상담 준비 버튼)
  const handleReady = () => {
    alert("[목업] 상담 준비(READY)");
    setStatus("READY");
  };

  return (
    <Card withBorder shadow="sm" p="md" radius="md">
      <Text fw={700}>상담 상태</Text>

      <Text size="sm" mt="xs">상태: {status}</Text>
      <Text size="sm">시작: {session.started_at ?? "-"}</Text>
      <Text size="sm">종료: {session.ended_at ?? "-"}</Text>
      <Text size="sm">경과: {session.duration_sec} 초</Text>

      {/* =========================
          상태에 따라 보이는 버튼들
      ========================== */}

      {status === "IN_PROGRESS" && (
        <Button
          color="red"
          fullWidth
          mt="md"
          radius="md"
          onClick={handleEnd}
        >
          상담 종료
        </Button>
      )}

      {status === "AFTER_CALL" && (
        <Button
          color="blue"
          fullWidth
          mt="md"
          radius="md"
          onClick={handleAfterCallDone}
        >
          후처리 완료 (READY)
        </Button>
      )}

      {status === "READY" && (
        <Button
          color="green"
          fullWidth
          mt="md"
          radius="md"
          onClick={handleReady}
        >
          상담 준비
        </Button>
      )}

      <Divider my="md" />

      <AfterCallForm session={session} />
    </Card>
  );
}
