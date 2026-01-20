import { useState, useEffect } from 'react';
import { 
  Container, Title, Card, Table, Badge, Button, Group, 
  Text, Stack, LoadingOverlay, ActionIcon, Tooltip, Box 
} from '@mantine/core';
import { notifications } from '@mantine/notifications';
import { IconRocket, IconRefresh, IconExternalLink } from '@tabler/icons-react';
import { adminApi } from "../../api/adminApi"; 

/**

 * 1. baseURL에 https://api.jhs-platform.co.kr가 이미 들어있으므로 
 * 2. 경로에서 중복되는 /api를 제거하거나 adminApi 설정에 맞게 조정합니다.
 */
const APPROVAL_API_BASE = '/hq/approvals';
const PUBLISH_API_BASE = '/hq/publish';
const ADMIN_HEADERS = { 'X-ADMIN-ID': '1' };

interface ApprovedDraft {
  eventDraftId: number;
  title: string;
  status: string;
  requestedAt: string;
}

export function HqPublishPage() {
  const [approvedDrafts, setApprovedDrafts] = useState<ApprovedDraft[]>([]);
  const [loading, setLoading] = useState(false);

  // 1. 발행 대기(승인 완료) 목록 로드
  const fetchApprovedDrafts = async () => {
    setLoading(true);
    try {
      //  [수정] axios.get -> adminApi.get
      const res = await adminApi.get(`${APPROVAL_API_BASE}/events/approved`, { 
        headers: ADMIN_HEADERS 
      });
      setApprovedDrafts(res.data);
    } catch (error) {
      notifications.show({ 
        title: '데이터 로드 실패', 
        message: '발행 대기 목록을 가져오지 못했습니다.', 
        color: 'red' 
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchApprovedDrafts(); }, []);

  // 2. 최종 발행(Publish) 실행
  const handlePublish = async (id: number, title: string) => {
    if (!window.confirm(`[${title}] 공연을 실시간 운영 서버에 발행하시겠습니까?`)) return;

    setLoading(true);
    try {
      /**
       *  [수정] axios.post -> adminApi.post
       * 이제 요청이 https://api.jhs-platform.co.kr/hq/publish/{id}로 정확히 날아갑니다.
       */
      await adminApi.post(`${PUBLISH_API_BASE}/${id}`, {}, { 
        headers: ADMIN_HEADERS 
      });

      notifications.show({ 
        title: '발행 완료', 
        message: '운영 DB(Event, Ticket, Seat) 반영이 성공적으로 끝났습니다.', 
        color: 'blue',
        icon: <IconRocket size={16} />
      });
      
      fetchApprovedDrafts(); // 목록 새로고침
    } catch (error: any) {
      //  백엔드의 IllegalStateException 메시지를 그대로 노출합니다. [cite: 2026-01-13]
      const errorMsg = error.response?.data?.message || '발행 중 서버 에러가 발생했습니다.';
      notifications.show({ 
        title: '발행 실패', 
        message: errorMsg, 
        color: 'red' 
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container size="xl" py="xl">
      <LoadingOverlay visible={loading} overlayProps={{ blur: 2 }} />
      
      <Group justify="space-between" mb="xl">
        <Box>
          <Title order={2} c="blue.9" style={{ letterSpacing: '-1px' }}>운영 발행(Publish) 관리</Title>
          <Text size="sm" c="dimmed">승인 완료된 Draft를 운영 스키마(chatmaching)로 이관합니다.</Text>
        </Box>
        <ActionIcon variant="light" size="lg" onClick={fetchApprovedDrafts} color="blue">
          <IconRefresh size={20} />
        </ActionIcon>
      </Group>

      <Card withBorder radius="md" shadow="sm" p={0}>
        <Table verticalSpacing="md" highlightOnHover>
          <Table.Thead bg="gray.0">
            <Table.Tr>
              <Table.Th style={{ paddingLeft: 20 }}>Draft ID</Table.Th>
              <Table.Th>승인된 공연명</Table.Th>
              <Table.Th>요청 일시</Table.Th>
              <Table.Th>현재 상태</Table.Th>
              <Table.Th>액션</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {approvedDrafts.length > 0 ? approvedDrafts.map((d) => (
              <Table.Tr key={d.eventDraftId}>
                <Table.Td style={{ paddingLeft: 20 }}>{d.eventDraftId}</Table.Td>
                <Table.Td>
                  <Group gap="xs">
                    <Text fw={600}>{d.title}</Text>
                    <Tooltip label="Draft 상세 보기">
                      <ActionIcon variant="subtle" size="sm" color="gray">
                        <IconExternalLink size={14} />
                      </ActionIcon>
                    </Tooltip>
                  </Group>
                </Table.Td>
                <Table.Td>{new Date(d.requestedAt).toLocaleString()}</Table.Td>
                <Table.Td>
                  <Badge color="green" variant="dot">APPROVED</Badge>
                </Table.Td>
                <Table.Td>
                  <Button 
                    color="dark" 
                    size="xs" 
                    leftSection={<IconRocket size={14} />}
                    onClick={() => handlePublish(d.eventDraftId, d.title)}
                  >
                    운영 서버 발행
                  </Button>
                </Table.Td>
              </Table.Tr>
            )) : (
              <Table.Tr>
                <Table.Td colSpan={5} style={{ textAlign: 'center' }} py={50}>
                  <Text c="dimmed">발행 가능한 승인 완료 건이 없습니다.</Text>
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