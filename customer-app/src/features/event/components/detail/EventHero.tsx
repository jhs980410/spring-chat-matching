// features/event/components/EventHero.tsx
import { Badge, Group, Image, Stack, Text } from "@mantine/core";
import type { EventDetail } from "../../types/eventTypes";

interface Props {
  event: EventDetail;
}

export default function EventHero({ event }: Props) {
  return (
    <Stack gap="sm">
      <Image
        src={event.thumbnail}
        radius="md"
        height={420}
        fallbackSrc="https://placehold.co/600x420?text=EVENT"
      />

      <Group justify="space-between">
        <Text fw={700} size="xl">
          {event.title}
        </Text>

        <Group>
          <Badge variant="light">{event.category}</Badge>
          <Badge
            color={
              event.status === "OPEN"
                ? "green"
                : event.status === "SOLD_OUT"
                ? "red"
                : "gray"
            }
          >
            {event.status}
          </Badge>
        </Group>
      </Group>
    </Stack>
  );
}
