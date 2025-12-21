import { Button, Card, Divider, Stack, Text } from "@mantine/core";
import type { EventDetail } from "../../types/eventTypes";

interface Props {
  event: EventDetail;
}

export default function EventInfoPanel({ event }: Props) {
  // 팝업 방식으로 변경했으므로 사용하지 않는 useLocation은 제거했습니다.

  const handleReserve = () => {
    const url = `/events/${event.id}/reserve`;
    
    // 팝업 창 설정
    const width = 1100;
    const height = 800;
    const left = window.screen.width / 2 - width / 2;
    const top = window.screen.height / 2 - height / 2;

    window.open(
      url,
      `reserve-${event.id}`,
      `width=${width},height=${height},left=${left},top=${top},resizable=yes,scrollbars=yes`
    );
  };

  return (
    <Card withBorder radius="md" p="lg" shadow="sm">
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
          color="blue"
          disabled={event.status !== "OPEN"}
          onClick={handleReserve}
        >
          {event.status === "OPEN" ? "예매하러 가기" : "판매 예정"}
        </Button>

        <Button variant="outline" fullWidth color="gray">
          공유하기
        </Button>

        {/* textAlign="center" 대신 ta="center"를 사용합니다 */}
        <Text size="xs" c="dimmed" ta="center">
          오픈 일정은 변경될 수 있습니다.
        </Text>
      </Stack>
    </Card>
  );
}