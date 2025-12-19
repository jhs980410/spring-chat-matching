import { Button, Card, Divider, Stack, Text } from "@mantine/core";
import { useNavigate, useLocation } from "react-router-dom";
import type { EventDetail } from "../../types/eventTypes";

interface Props {
  event: EventDetail;
}

export default function EventInfoPanel({ event }: Props) {
  const navigate = useNavigate();
  const location = useLocation();

  const handleReserve = () => {
    navigate(`/events/${event.id}/reserve`, {
      state: { from: location.pathname },
    });
  };

  return (
    <Card withBorder radius="md" p="lg">
      <Stack gap="md">
        <Text fw={600}>공연 일정</Text>

        <Text size="sm">
          {event.startAt.replace("T", " ")} <br />
          ~ {event.endAt.replace("T", " ")}
        </Text>

        <Divider />

        <Button
          fullWidth
          size="md"
          disabled={event.status !== "OPEN"}
          onClick={handleReserve}
        >
          예매하러 가기
        </Button>

        <Button variant="outline" fullWidth>
          공유하기
        </Button>

        <Text size="xs" c="dimmed">
          오픈 일정은 변경될 수 있습니다.
        </Text>
      </Stack>
    </Card>
  );
}
