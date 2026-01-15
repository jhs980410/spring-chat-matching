import { useState, useEffect } from 'react';
import { 
  Container, Title, Card, Table, Badge, Button, Group, 
  Text, Modal, Stack, Divider, Image, SimpleGrid, LoadingOverlay, Textarea 
} from '@mantine/core';
import { notifications } from '@mantine/notifications';
import { IconCheck, IconX, IconRocket, IconSearch } from '@tabler/icons-react';

// 1. 목록용 더미 데이터 (EventDraftSummaryResponse 구조)
const DUMMY_LIST = [
  { id: 1, title: "2026 윈터 재즈 페스티벌", status: "REQUESTED", requestedAt: "2026-01-15T10:00:00" },
  { id: 2, title: "현대미술 기획전: 빛의 형태", status: "APPROVED", requestedAt: "2026-01-14T14:30:00" },
  { id: 3, title: "뮤지컬 <라흐마니노프>", status: "REQUESTED", requestedAt: "2026-01-15T09:15:00" },
];

// 2. 상세용 더미 데이터 (EventDraftDetailResponse 구조)
const DUMMY_DETAIL_DATA: Record<number, any> = {
  1: {
    id: 1,
    title: "2026 윈터 재즈 페스티벌",
    description: "한겨울 밤의 낭만을 더해줄 재즈 아티스트들의 향연",
    venue: "세종문화회관 대극장",
    startAt: "2026-02-10T19:00:00",
    endAt: "2026-02-12T22:00:00",
    thumbnail: "https://images.unsplash.com/photo-1511192336575-5a79af67a629?w=800",
    status: "REQUESTED",
    tickets: [
      { id: 101, name: "VIP석", price: 150000, totalQuantity: 50 },
      { id: 102, name: "R석", price: 120000, totalQuantity: 150 },
      { id: 103, name: "S석", price: 80000, totalQuantity: 300 }
    ]
  },
  2: {
    id: 2,
    title: "현대미술 기획전: 빛의 형태",
    description: "디지털 미디어와 빛을 이용한 현대미술의 재해석",
    venue: "DDP 배움터",
    startAt: "2026-03-01T10:00:00",
    endAt: "2026-05-31T18:00:00",
    thumbnail: "https://images.unsplash.com/photo-1554188248-986adbb73be4?w=800",
    status: "APPROVED",
    tickets: [{ id: 201, name: "일반입장권", price: 20000, totalQuantity: 5000 }]
  }
};

export function HqApprovalPage() {
  const [drafts, setDrafts] = useState<any[]>([]);
  const [selectedDraft, setSelectedDraft] = useState<any | null>(null);
  const [detailOpened, setDetailOpened] = useState(false);
  const [rejectModalOpened, setRejectModalOpened] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [loading, setLoading] = useState(false);

  // 초기 데이터 로드 시뮬레이션
  useEffect(() => {
    setLoading(true);
    setTimeout(() => {
      setDrafts(DUMMY_LIST);
      setLoading(false);
    }, 600);
  }, []);

  // 상세 조회 시뮬레이션
  const handleViewDetail = (id: number) => {
    setLoading(true);
    setTimeout(() => {
      setSelectedDraft(DUMMY_DETAIL_DATA[id] || DUMMY_DETAIL_DATA[1]);
      setDetailOpened(true);
      setLoading(false);
    }, 400);
  };

  // 승인 시뮬레이션
  const handleApprove = (id: number) => {
    notifications.show({ title: '승인 처리됨', message: `ID: ${id} 공연이 승인되었습니다.`, color: 'green', icon: <IconCheck size={16}/> });
    setDrafts(prev => prev.map(d => d.id === id ? { ...d, status: 'APPROVED' } : d));
    setDetailOpened(false);
  };

  // 반려 시뮬레이션
  const handleReject = () => {
    notifications.show({ title: '반려 처리됨', message: `사유: ${rejectReason}`, color: 'red', icon: <IconX size={16}/> });
    setRejectModalOpened(false);
    setDetailOpened(false);
    setRejectReason('');
  };

  // 발행 시뮬레이션
  const handlePublish = (id: number) => {
    notifications.show({ title: '운영 발행 성공', message: '공연이 실시간 서버에 반영되었습니다.', color: 'blue', icon: <IconRocket size={16}/> });
  };

  return (
    <Container size="xl" py="xl">
      <LoadingOverlay visible={loading} overlayProps={{ blur: 2 }} />
      <Title order={2} mb="xl">HQ Admin: 초안 검토 및 발행</Title>

      <Card withBorder radius="md" shadow="xs">
        <Table verticalSpacing="md" highlightOnHover>
          <Table.Thead bg="gray.0">
            <Table.Tr>
              <Table.Th>ID</Table.Th>
              <Table.Th>공연 제목</Table.Th>
              <Table.Th>상태</Table.Th>
              <Table.Th>작업</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {drafts.map((d) => (
              <Table.Tr key={d.id}>
                <Table.Td>{d.id}</Table.Td>
                <Table.Td fw={500}>{d.title}</Table.Td>
                <Table.Td>
                  <Badge color={d.status === 'APPROVED' ? 'green' : d.status === 'REQUESTED' ? 'blue' : 'gray'} variant="light">
                    {d.status}
                  </Badge>
                </Table.Td>
                <Table.Td>
                  <Group gap="xs">
                    <Button variant="light" size="xs" leftSection={<IconSearch size={14} />} onClick={() => handleViewDetail(d.id)}>검토</Button>
                    {d.status === 'APPROVED' && (
                      <Button color="dark" size="xs" leftSection={<IconRocket size={14} />} onClick={() => handlePublish(d.id)}>Publish</Button>
                    )}
                  </Group>
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      </Card>

      {/* 상세 검토 모달 */}
      <Modal opened={detailOpened} onClose={() => setDetailOpened(false)} title="공연 상세 정보 검토" size="xl">
        {selectedDraft && (
          <Stack>
            <SimpleGrid cols={2} spacing="xl">
              <Image src={selectedDraft.thumbnail} radius="md" fallbackSrc="https://via.placeholder.com/400x500?text=No+Image" />
              <Stack gap="xs">
                <Title order={3}>{selectedDraft.title}</Title>
                <Text size="sm" c="dimmed">{selectedDraft.description}</Text>
                <Divider my="xs" />
                <Text size="sm"><b>장소:</b> {selectedDraft.venue}</Text>
                <Text size="sm"><b>기간:</b> {new Date(selectedDraft.startAt).toLocaleDateString()} ~ {new Date(selectedDraft.endAt).toLocaleDateString()}</Text>
                <Divider my="xs" />
                <Text fw={700} size="sm">티켓 구성</Text>
                {selectedDraft.tickets?.map((t: any) => (
                  <Group key={t.id} justify="space-between">
                    <Text size="xs">{t.name} ({t.totalQuantity}석)</Text>
                    <Text size="xs" fw={600}>{t.price.toLocaleString()}원</Text>
                  </Group>
                ))}
              </Stack>
            </SimpleGrid>
            
            <Divider my="md" />
            
            <Group justify="flex-end">
              <Button color="red" variant="outline" leftSection={<IconX size={16}/>} onClick={() => setRejectModalOpened(true)}>반려</Button>
              <Button color="green" leftSection={<IconCheck size={16}/>} onClick={() => handleApprove(selectedDraft.id)}>최종 승인</Button>
            </Group>
          </Stack>
        )}
      </Modal>

      {/* 반려 사유 모달 */}
      <Modal opened={rejectModalOpened} onClose={() => setRejectModalOpened(false)} title="반려 사유 입력">
        <Textarea 
          label="매니저 전달 메시지"
          placeholder="반려 사유를 상세히 입력하세요." 
          value={rejectReason} 
          onChange={(e) => setRejectReason(e.currentTarget.value)} 
          minRows={3} 
        />
        <Button fullWidth mt="md" color="red" onClick={handleReject}>반려 확정</Button>
      </Modal>
    </Container> 
  );
}

export default HqApprovalPage;