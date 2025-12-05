import { useState } from "react";
import { Text, Select, NumberInput, Textarea, Button, Group } from "@mantine/core";
// 네 axios 인스턴스 경로로 변경
import api from "../../../api/axios";
import { notifications } from "@mantine/notifications";

interface AfterCallFormProps {
  session: any; // 필요하면 타입 나중에 별도 정의
  onSaved?: () => void; // 저장 후 부모에서 갱신 필요할 때
}

export default function AfterCallForm({ session, onSaved }: AfterCallFormProps) {
  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    endReason: session.end_reason ?? "",
    satisfactionScore:
      typeof session.satisfaction_score === "number"
        ? session.satisfaction_score
        : undefined,
    afterCallSec: session.after_call_sec ?? 0,
    feedback: session.feedback ?? "",
  });

  // =========================
  // 저장 실행
  // =========================
  const handleSave = async () => {
    if (!session?.id) {
      notifications.show({
        color: "red",
        message: "세션 ID가 없습니다.",
      });
      return;
    }

    setSaving(true);

    try {
      await api.patch(`/sessions/${session.id}/after-call`, {
        endReason: form.endReason,
        satisfactionScore: form.satisfactionScore,
        afterCallSec: form.afterCallSec,
        feedback: form.feedback,
      });

      notifications.show({
        color: "blue",
        message: "상담 결과가 저장되었습니다.",
      });

      if (onSaved) onSaved();
    } catch (e) {
      console.error(e);
      notifications.show({
        color: "red",
        message: "저장 중 오류가 발생했습니다.",
      });
    } finally {
      setSaving(false);
    }
  };

  // =========================
  // 렌더
  // =========================
  return (
    <>
      <Text fw={700} mb="xs">
        상담 결과 / 메모
      </Text>

      {/* 종료 사유 */}
      <Select
        label="종료 사유"
        placeholder="선택"
        data={[
          { value: "USER", label: "고객 종료" },
          { value: "COUNSELOR", label: "상담사 종료" },
          { value: "TIMEOUT", label: "시간 초과" },
          { value: "ADMIN", label: "관리자 종료" },
        ]}
        value={form.endReason}
        onChange={(v) => setForm((p) => ({ ...p, endReason: v ?? "" }))}
      />

      {/* 만족도 + After-call */}
      <Group grow mt="sm">
        <NumberInput
          label="만족도(1~5)"
          min={1}
          max={5}
          value={form.satisfactionScore}
          onChange={(v) =>
            setForm((p) => ({
              ...p,
              satisfactionScore: typeof v === "number" ? v : undefined,
            }))
          }
        />

        <NumberInput
          label="After-Call(초)"
          min={0}
          value={form.afterCallSec}
          onChange={(v) =>
            setForm((p) => ({
              ...p,
              afterCallSec: typeof v === "number" ? v : 0,
            }))
          }
        />
      </Group>

      {/* 상담 메모 */}
      <Textarea
        label="상담 메모"
        minRows={3}
        value={form.feedback}
        onChange={(e) =>
          setForm((p) => ({
            ...p,
            feedback: e.currentTarget.value,
          }))
        }
        mt="sm"
      />

      {/* 저장 버튼 */}
      <Button
        variant="outline"
        fullWidth
        mt="md"
        radius="md"
        loading={saving}
        onClick={handleSave}
      >
        상담 결과 저장
      </Button>
    </>
  );
}
