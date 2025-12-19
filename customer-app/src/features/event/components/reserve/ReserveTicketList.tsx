import { Card, Stack, Text, Badge } from "@mantine/core";
import type { TicketOption } from "../../types/reserveTypes";

interface Props {
  tickets: TicketOption[];
  selected: TicketOption | null;
  onSelect: (ticket: TicketOption) => void;
}

export default function ReserveTicketList({
  tickets,
  selected,
  onSelect,
}: Props) {
  return (
    <Card withBorder radius="md" p="lg">
      <Stack>
        <Text fw={600}>좌석 선택</Text>

        {tickets.map((t) => (
          <Card
            key={t.id}
            withBorder
            radius="md"
            p="md"
            style={{
              cursor: "pointer",
              border:
                selected?.id === t.id
                  ? "2px solid #228be6"
                  : undefined,
            }}
            onClick={() => onSelect(t)}
          >
            <Stack gap={4}>
              <Text fw={500}>{t.name}</Text>
              <Text size="sm">{t.price.toLocaleString()}원</Text>
              <Badge size="sm" color="blue">
                잔여 {t.remain}
              </Badge>
            </Stack>
          </Card>
        ))}
      </Stack>
    </Card>
  );
}
