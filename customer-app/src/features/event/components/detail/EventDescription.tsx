// features/event/components/EventDescription.tsx
import { Stack, Text, Title } from "@mantine/core";
import type { EventDetail } from "../../types/eventTypes";

interface Props {
  event: EventDetail;
}

export default function EventDescription({ event }: Props) {
  return (
    <Stack gap="sm">
      <Title order={4}>공연 소개</Title>

      <Text size="sm" style={{ whiteSpace: "pre-line" }}>
        {event.description}
      </Text>
    </Stack>
  );
}
