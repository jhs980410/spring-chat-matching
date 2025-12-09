import { Card, Text, Button, Divider, MultiSelect } from "@mantine/core";
import { useState, useEffect } from "react";
import { notifications } from "@mantine/notifications";
import api from "../../../api/axios";
import AfterCallForm from "./AfterCallForm";

export default function ChatStatusPanel({ session }: any) {

  const calcStatus = (s: any) => {
    if (s.endedAt) return "ENDED";
    if (s.afterCallEndedAt) return "AFTER_CALL";
    if (s.startedAt) return "IN_PROGRESS";
    return "WAITING";
  };

  const [status, setStatus] = useState(calcStatus(session));
  const [loading, setLoading] = useState(false);
  const [categories, setCategories] = useState<any[]>([]);
  const [selectedCategoryIds, setSelectedCategoryIds] = useState<any[]>([]);

  // 🔥 session이 변경될 때마다 화면 상태 갱신
  useEffect(() => {
    setStatus(calcStatus(session));
  }, [session]);

  // 카테고리 목록 조회
  useEffect(() => {
    api.get("/categories", { withCredentials: true })
      .then((res) => {
        setCategories(
          res.data.map((c: any) => ({
            value: c.id.toString(),
            label: `${c.domainName} - ${c.name}`,
          }))
        );
      })
      .catch((e) => console.error(e));
  }, []);

  // 상담 종료
  const handleEnd = async () => {
    if (!session?.sessionId) return;

    setLoading(true);
    try {
      await api.patch(`/sessions/${session.sessionId}/end`);

      notifications.show({ color: "red", message: "상담 종료되었습니다." });

      setStatus("AFTER_CALL");
    } catch (e) {
      notifications.show({ color: "red", message: "상담 종료 실패" });
    } finally {
      setLoading(false);
    }
  };

  // AfterCall 완료
  const handleAfterCallDone = () => {
    notifications.show({ color: "blue", message: "후처리 저장 완료" });
    setStatus("AFTER_CALL");
  };

  // READY
  const handleReady = async () => {
    if (selectedCategoryIds.length === 0) {
      notifications.show({
        color: "red",
        message: "카테고리를 하나 이상 선택해야 합니다!",
      });
      return;
    }

    try {
      await api.patch("/counselors/ready", {
        categoryIds: selectedCategoryIds.map((v) => Number(v)),
      });

      notifications.show({
        color: "green",
        message: "상담 준비 완료되었습니다 (READY)",
      });

      setStatus("READY");
    } catch (e) {
      notifications.show({ color: "red", message: "READY 실패" });
    }
  };

  return (
    <Card withBorder shadow="sm" p="md" radius="md">
      <Text fw={700}>상담 상태</Text>

      <Text size="sm" mt="xs">상태: {status}</Text>
      <Text size="sm">시작: {session.startedAt ?? "-"}</Text>
      <Text size="sm">종료: {session.endedAt ?? "-"}</Text>
      <Text size="sm">경과: {session.durationSec ?? "-"} 초</Text>

      <Divider my="sm" />

      {/* 🔥 IN_PROGRESS 상태면 즉시 종료 버튼 표시 */}
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

      {/* 🔥 WAITING / END 상태면 READY UI */}
      {status !== "IN_PROGRESS" && (
        <>
          <Text size="sm" mb="xs">상담 가능 카테고리 선택</Text>

          <MultiSelect
            placeholder="카테고리를 선택하세요"
            data={categories}
            value={selectedCategoryIds}
            onChange={setSelectedCategoryIds}
            searchable
            mb="md"
          />

          <Button color="blue" fullWidth radius="md" onClick={handleReady}>
            상담 준비 (READY)
          </Button>
        </>
      )}

      <Divider my="md" />

      {/* AfterCall 저장 */}
      <AfterCallForm session={session} onSaved={handleAfterCallDone} />
    </Card>
  );
}
