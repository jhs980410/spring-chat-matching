import { Button, Card, Divider, Stack, Text } from "@mantine/core";
import type { TicketOption } from "../../types/reserveTypes";

interface Props {
  selected: TicketOption | null;
}

export default function ReserveSummaryPanel({ selected }: Props) {
  return (
    <Card withBorder radius="md" p="lg">
      <Stack gap="md">
        <Text fw={600}>예매 요약</Text>

        {selected ? (
          <>
            <Text size="sm">좌석: {selected.name}</Text>
            <Text size="sm">
              가격: {selected.price.toLocaleString()}원
            </Text>
          </>
        ) : (
          <Text size="sm" c="dimmed">
            좌석을 선택하세요
          </Text>
        )}

        <Divider />

        <Text fw={700}>
          총 금액:{" "}
          {selected ? selected.price.toLocaleString() : 0}원
        </Text>

        <Button fullWidth disabled={!selected}>
          결제하기
        </Button>
      </Stack>
    </Card>
  );
}
