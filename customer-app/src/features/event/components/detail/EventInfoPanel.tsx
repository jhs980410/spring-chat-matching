import { Button, Card, Divider, Stack, Text, Modal, Loader, Progress, Group, ActionIcon } from "@mantine/core";
import { useState, useEffect, useRef } from "react";
import type { EventDetail } from "../../types/eventTypes";
import api from "../../../../api/axios"; // 설정하신 axios 인스턴스

interface Props {
  event: EventDetail;
}

export default function EventInfoPanel({ event }: Props) {
  // --- 대기열 관련 상태 ---
  const [isWaiting, setIsWaiting] = useState(false);
  const [waitingRank, setWaitingRank] = useState<number | null>(null);
  const pollingInterval = useRef<NodeJS.Timeout | null>(null);

  /**
   * 1. 실제 예약 팝업창을 여는 함수
   */
  const openReserveWindow = () => {
    const url = `/events/${event.id}/reserve`;
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

  /**
   * 2. 상태를 체크하는 폴링 함수
   */
  const startPolling = () => {
    // 2초 간격으로 상태 조회
    pollingInterval.current = setInterval(async () => {
      try {
        const { data } = await api.get(`/waiting-room/${event.id}/status`);

        if (data.status === "AVAILABLE") {
          stopPolling();
          setIsWaiting(false);
          openReserveWindow(); // 권한 획득 시 팝업 오픈
        } else if (data.status === "WAITING") {
          setWaitingRank(data.rank); // 순번 업데이트
        } else if (data.status === "EXPIRED") {
          stopPolling();
          setIsWaiting(false);
          alert("대기 시간이 만료되었습니다. 다시 시도해주세요.");
        }
      } catch (error) {
        console.error("대기열 상태 확인 실패:", error);
      }
    }, 2000);
  };

  const stopPolling = () => {
    if (pollingInterval.current) {
      clearInterval(pollingInterval.current);
      pollingInterval.current = null;
    }
  };

  /**
   * 3. [예매하러 가기] 버튼 클릭 핸들러
   */
  const handleReserveClick = async () => {
    try {
      // 대기열 진입 요청 (JWT 쿠키가 함께 전송됨)
      const { data } = await api.post(`/waiting-room/${event.id}/join`);

      if (data.status === "AVAILABLE") {
        openReserveWindow(); // 즉시 입장 가능한 경우
      } else {
        setWaitingRank(data.rank);
        setIsWaiting(true); // 모달 표시
        startPolling();    // 대기 시작
      }
    } catch (error) {
      alert("대기열 서비스에 접속할 수 없습니다.");
    }
  };

  // 컴포넌트 언마운트 시 폴링 정리
  useEffect(() => {
    return () => stopPolling();
  }, []);

  return (
    <>
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
            onClick={handleReserveClick} // 수정된 핸들러
          >
            {event.status === "OPEN" ? "예매하러 가기" : "판매 예정"}
          </Button>

          <Button variant="outline" fullWidth color="gray">
            공유하기
          </Button>

          <Text size="xs" c="dimmed" ta="center">
            오픈 일정은 변경될 수 있습니다.
          </Text>
        </Stack>
      </Card>

      {/* --- 대기열 UI 모달 --- */}
      <Modal
        opened={isWaiting}
        onClose={() => {}} // 닫기 버튼 비활성화 (이탈 방지)
        withCloseButton={false}
        centered
        radius="lg"
        size="md"
      >
        <Stack align="center" gap="xl" py="lg">
          <Loader size="xl" variant="dots" color="blue" />
          
          <Stack align="center" gap={5}>
            <Text fw={700} size="xl" c="blue">현재 접속 대기 중입니다</Text>
            <Text size="md" fw={500}>
              내 앞 대기 인원: <Text span c="blue" fw={800}>{waitingRank}</Text>명
            </Text>
          </Stack>

          <Stack w="100%" gap="xs">
            <Progress value={100} animated striped color="blue" size="sm" />
            <Text size="xs" c="dimmed" ta="center">
              잠시만 기다려 주시면 자동으로 예매 창으로 이동합니다.
            </Text>
          </Stack>

          <Divider w="100%" />

          <Stack gap={4}>
            <Text size="xs" c="red" ta="center" fw={600}>
              ※ 주의: 새로고침하거나 창을 닫으면 대기 순번이 초기화됩니다.
            </Text>
            <Button 
              variant="subtle" 
              color="gray" 
              size="xs"
              onClick={() => {
                stopPolling();
                setIsWaiting(false);
              }}
            >
              대기 취소하기
            </Button>
          </Stack>
        </Stack>
      </Modal>
    </>
  );
}