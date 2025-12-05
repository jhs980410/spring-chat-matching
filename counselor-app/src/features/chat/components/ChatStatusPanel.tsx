import { Card, Text, Button, Divider } from "@mantine/core";
import { useState } from "react";
import { notifications } from "@mantine/notifications";
import api from "../../../api/axios"; // 네 axios 인스턴스
import AfterCallForm from "./AfterCallForm";

export default function ChatStatusPanel({ session }: any) {
  // =========================
  // 1) 초기 상태 계산
  // =========================
  const initialStatus = session.ended_at
    ? "ENDED"
    : session.end_reason
    ? "AFTER_CALL"
    : session.started_at
    ? "IN_PROGRESS"
    : "WAITING";

  const [status, setStatus] = useState(initialStatus);
  const [loading, setLoading] = useState(false);

  // =========================
  // 2) 상담 종료 → ENDED   (PATCH /end)
  // =========================
  const handleEnd = async () => {
    if (!session?.id) return;

    setLoading(true);
    try {
      await api.patch(`/sessions/${session.id}/end`);

      notifications.show({
        color: "red",
        message: "상담이 종료되었습니다.",
      });

      setStatus("AFTER_CALL");
    } catch (e) {
      console.error(e);
      notifications.show({
        color: "red",
        message: "상담 종료 실패",
      });
    } finally {
      setLoading(false);
    }
  };

  // =========================
  // 3) After-Call 저장 완료 → READY
  // =========================
  const handleAfterCallDone = () => {
    notifications.show({
      color: "blue",
      message: "후처리가 완료되었습니다.",
    });
    setStatus("READY");
  };

  // =========================
  // 4) READY → READY 상태로 서버 반영  (PATCH /ready)
  // =========================
  const handleReady = async () => {
    try {
      await api.patch("/counselors/ready", {
        categoryIds: [session.category_id], // 필요하면 변경
      });

      notifications.show({
        color: "green",
        message: "상담 준비 상태가 되었습니다.",
      });

      setStatus("READY");
    } catch (e) {
      console.error(e);
      notifications.show({
        color: "red",
        message: "READY 설정 실패",
      });
    }
  };

  return (
    <Card withBorder shadow="sm" p="md" radius="md">
      <Text fw={700}>상담 상태</Text>

      <Text size="sm" mt="xs">상태: {status}</Text>
      <Text size="sm">시작: {session.started_at ?? "-"}</Text>
      <Text size="sm">종료: {session.ended_at ?? "-"}</Text>
      <Text size="sm">경과: {session.duration_sec} 초</Text>

      {/* ============================
          상태별 버튼 표시
      ============================= */}

      {status === "IN_PROGRESS" && (
        <Button
          color="red"
          fullWidth
          mt="md"
          radius="md"
          loading={loading}
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

      {/* AfterCall 저장하면 READY로 전환 */}
      <AfterCallForm session={session} onSaved={handleAfterCallDone} />
    </Card>
  );
}
