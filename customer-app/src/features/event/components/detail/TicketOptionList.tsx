// features/event/components/TicketOptionList.tsx
import { Badge, Card, Group, Stack, Text } from "@mantine/core";
import type { TicketOption } from "../../pages/types/eventTypes";

interface Props {
  tickets: TicketOption[];
}

export default function TicketOptionList({ tickets }: Props) {
  return (
    <Stack gap="sm">
      <Text fw={600}>티켓 정보</Text>

      {tickets.map(ticket => (
        <Card key={ticket.ticketId} withBorder radius="md" p="md">
          <Group justify="space-between">
            <Stack gap={2}>
              <Text fw={500}>{ticket.name}</Text>
              <Text size="sm" c="dimmed">
                {ticket.price.toLocaleString()}원
              </Text>
            </Stack>

            {ticket.soldOut ? (
              <Badge color="red">매진</Badge>
            ) : (
              <Badge variant="outline">
                잔여 {ticket.remainQuantity}
              </Badge>
            )}
          </Group>
        </Card>
      ))}
    </Stack>
  );
}
