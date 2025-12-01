import { useState } from "react";
import { Text, Select, NumberInput, Textarea, Button, Group } from "@mantine/core";

export default function AfterCallForm({ session }: any) {
  const [form, setForm] = useState({
    endReason: session.end_reason ?? "",
    satisfactionScore: session.satisfaction_score ?? undefined,
    afterCallSec: session.after_call_sec ?? 0,
    feedback: session.feedback ?? ""
  });

  const handleSave = () => {
    console.log("[AfterCall 저장]", form);
    alert("[목업] 상담 결과 저장됨");
  };

  return (
    <>
      <Text fw={700} mb="xs">
        상담 결과 / 메모
      </Text>

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

      <Button
        variant="outline"
        fullWidth
        mt="md"
        radius="md"
        onClick={handleSave}
      >
        상담 결과 저장 (목업)
      </Button>
    </>
  );
}
