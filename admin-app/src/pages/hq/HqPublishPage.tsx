import { useState, useEffect } from 'react';
import { 
  Container, Title, Card, Table, Badge, Button, Group, 
  Text, Stack, LoadingOverlay, ActionIcon, Tooltip 
} from '@mantine/core';
import { notifications } from '@mantine/notifications';
import { IconRocket, IconRefresh, IconExternalLink } from '@tabler/icons-react';
// import { publishEvent } from '../../api/adminApi'; // 추후 주석 해제

// 발행 대기 중인(APPROVED) 더미 데이터
const DUMMY_APPROVED_LIST = [
  { 
    id: 102, 
    title: "임영웅 전국 투어 콘서트 - 서울", 
    status: "APPROVED", 
    approvedAt: "2026-01-13T14:20:00",
    manager: "김매니저"
  },
  { 
    id: 105, 
    title: "싸이 흠뻑쇼 2026", 
    status: "APPROVED", 
    approvedAt: "2026-01-15T11:00:00",
    manager: "이매니저"
  }
];

export function HqPublishPage() {
  const [approvedDrafts, setApprovedDrafts] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  // 1. 승인된 목록 로드 시뮬레이션
  const fetchApprovedDrafts = () => {
    setLoading(true);
    setTimeout(() => {
      setApprovedDrafts(DUMMY_APPROVED_LIST);
      setLoading(false);
    }, 500);
  };

  useEffect(() => { fetchApprovedDrafts(); }, []);

  // 2. 최종 발행(Publish) 시뮬레이션
  const handlePublish = async (id: number, title: string) => {
    if (!window.confirm(`[${title}] 공연을 실시간 운영 서버에 발행하시겠습니까?`)) return;

    setLoading(true);
    // API 연결 시: await publishEvent(id);
    setTimeout(() => {
      notifications.show({ 
        title: '발행 완료', 
        message: '공연 정보가 운영 환경에 성공적으로 반영되었습니다.', 
        color: 'blue',
        icon: <IconRocket size={16} />
      });
      // 발행 성공 후 목록에서 제거 시뮬레이션
      setApprovedDrafts(prev => prev.filter(d => d.id !== id));
      setLoading(false);
    }, 1000);
  };

  return (
    <Container size="xl" py="xl">
      <LoadingOverlay visible={loading} overlayProps={{ blur: 2 }} />
      
      <Group justify="space-between" mb="xl">
        <Stack gap={0}>
          <Title order={2}>운영 발행(Publish) 관리</Title>
          <Text size="sm" c="dimmed">본사 승인이 완료된 항목을 최종적으로 유저 서비스에 배포합니다.</Text>
        </Stack>
        <ActionIcon variant="light" size="lg" onClick={fetchApprovedDrafts} title="새로고침">
          <IconRefresh size={20} />
        </ActionIcon>
      </Group>

      <Card withBorder radius="md" shadow="sm">
        <Table verticalSpacing="md" highlightOnHover>
          <Table.Thead bg="gray.0">
            <Table.Tr>
              <Table.Th>ID</Table.Th>
              <Table.Th>승인된 공연명</Table.Th>
              <Table.Th>담당 매니저</Table.Th>
              <Table.Th>승인 일시</Table.Th>
              <Table.Th>상태</Table.Th>
              <Table.Th>최종 작업</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {approvedDrafts.length > 0 ? approvedDrafts.map((d) => (
              <Table.Tr key={d.id}>
                <Table.Td>{d.id}</Table.Td>
                <Table.Td>
                  <Group gap="xs">
                    <Text fw={600}>{d.title}</Text>
                    <Tooltip label="상세보기">
                      <ActionIcon variant="subtle" size="sm" color="gray">
                        <IconExternalLink size={14} />
                      </ActionIcon>
                    </Tooltip>
                  </Group>
                </Table.Td>
                <Table.Td>{d.manager}</Table.Td>
                <Table.Td>{new Date(d.approvedAt).toLocaleString()}</Table.Td>
                <Table.Td>
                  <Badge color="green" variant="dot">READY</Badge>
                </Table.Td>
                <Table.Td>
                  <Button 
                    color="dark" 
                    size="xs" 
                    leftSection={<IconRocket size={14} />}
                    onClick={() => handlePublish(d.id, d.title)}
                  >
                    지금 발행
                  </Button>
                </Table.Td>
              </Table.Tr>
            )) : (
              <Table.Tr>
                <Table.Td colSpan={6} align="center" py={50}>
                  <Text c="dimmed">현재 발행 대기 중인 공연이 없습니다.</Text>
                </Table.Td>
              </Table.Tr>
            )}
          </Table.Tbody>
        </Table>
      </Card>
    </Container>
  );
}

export default HqPublishPage;