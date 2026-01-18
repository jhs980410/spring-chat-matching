import { useState, useEffect } from 'react';
import { 
  Container, Title, Card, Table, Badge, Button, Group, 
  Text, Modal, Stack, Divider, SimpleGrid, LoadingOverlay, 
  Textarea, Tabs, Box, ScrollArea 
} from '@mantine/core'; 
import { notifications } from '@mantine/notifications';
import { IconCheck, IconX, IconSearch, IconFileCertificate, IconTicket } from '@tabler/icons-react';
import axios from 'axios';

// --- 인터페이스 정의 (백엔드 DTO 구조와 일치) ---
interface ContractDraft {
  id: number;
  businessName: string;
  issueMethod: string;
  requestedAt: string;
  businessNumber?: string;
  settlementEmail?: string;
}

interface EventDraft {
  eventDraftId: number;
  title: string;
  status: string;
  requestedAt: string;
}

/**
 * ❗ [핵심 수정] 하드코딩된 localhost 주소를 제거했습니다.
 * 운영 환경의 Nginx 설정(proxy_pass http://hq-admin:8082)과 연동됩니다.
 */
const API_BASE = '/api/hq/approvals';
const ADMIN_HEADERS = { 'X-ADMIN-ID': '1' };

export function HqApprovalPage() {
  const [activeTab, setActiveTab] = useState<string | null>('contracts');
  const [contracts, setContracts] = useState<ContractDraft[]>([]);
  const [events, setEvents] = useState<EventDraft[]>([]);
  const [selectedItem, setSelectedItem] = useState<any | null>(null);
  const [detailOpened, setDetailOpened] = useState(false);
  const [rejectModalOpened, setRejectModalOpened] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [loading, setLoading] = useState(false);

  // 데이터 로드 함수
  const fetchData = async () => {
    setLoading(true);
    try {
      const url = activeTab === 'contracts' ? '/contracts/pending' : '/events/pending';
      // 상대 경로를 사용하여 요청을 보냅니다.
      const res = await axios.get(`${API_BASE}${url}`, { headers: ADMIN_HEADERS });
      if (activeTab === 'contracts') setContracts(res.data);
      else setEvents(res.data);
    } catch (error) {
      notifications.show({ 
        title: '데이터 로드 실패', 
        message: 'API 서버 상태를 확인하세요.', 
        color: 'red' 
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, [activeTab]);

  // 승인 처리 함수
  const handleApprove = async () => {
    if (!selectedItem) return;
    setLoading(true);
    const targetId = activeTab === 'contracts' ? selectedItem.id : selectedItem.eventDraftId;
    const endpoint = activeTab === 'contracts' ? `/contracts/${targetId}/approve` : `/events/${targetId}/approve`;

    try {
      await axios.post(`${API_BASE}${endpoint}`, {}, { headers: ADMIN_HEADERS });
      notifications.show({ 
        title: '승인 완료', 
        message: '성공적으로 승인되었습니다.', 
        color: 'green', 
        icon: <IconCheck size={16}/> 
      });
      setDetailOpened(false);
      fetchData();
    } catch (error) {
      notifications.show({ title: '승인 실패', message: '오류가 발생했습니다.', color: 'red' });
    } finally {
      setLoading(false);
    }
  };

  // 반려 처리 함수
  const handleReject = async () => {
    if (!selectedItem || !rejectReason.trim()) {
        notifications.show({ title: '반려 불가', message: '사유를 입력해주세요.', color: 'yellow' });
        return;
    }
    const targetId = activeTab === 'contracts' ? selectedItem.id : selectedItem.eventDraftId;
    try {
      // 반려 엔드포인트도 상대 경로를 적용합니다.
      await axios.post(`${API_BASE}/events/${targetId}/reject`, { reason: rejectReason }, { headers: ADMIN_HEADERS });
      notifications.show({ 
        title: '반려 완료', 
        message: '반려 처리가 완료되었습니다.', 
        color: 'orange', 
        icon: <IconX size={16}/> 
      });
      setRejectModalOpened(false);
      setDetailOpened(false);
      setRejectReason('');
      fetchData();
    } catch (error) {
      notifications.show({ title: '반려 실패', message: '서버 응답을 확인하세요.', color: 'red' });
    }
  };

  return (
    <Container size="xl" py="xl">
      <LoadingOverlay visible={loading} overlayProps={{ blur: 2 }} />
      <Box mb="xl">
        <Title order={2} c="blue.9">HQ Admin: 통합 승인 센터</Title>
        <Text size="sm" c="dimmed">계약 및 공연 초안을 검토합니다.</Text>
      </Box>

      <Tabs value={activeTab} onChange={setActiveTab} variant="outline">
        <Tabs.List mb="lg">
          <Tabs.Tab value="contracts" leftSection={<IconFileCertificate size={16}/>}>판매 계약 검토</Tabs.Tab>
          <Tabs.Tab value="events" leftSection={<IconTicket size={16}/>}>공연 초안 검토</Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="contracts">
          <Card withBorder radius="md" p={0}>
            <ScrollArea>
              <Table verticalSpacing="md" highlightOnHover>
                <Table.Thead bg="gray.0">
                  <Table.Tr>
                    <Table.Th style={{ paddingLeft: 20 }}>ID</Table.Th>
                    <Table.Th>업체명</Table.Th>
                    <Table.Th>발권 방식</Table.Th>
                    <Table.Th>요청일시</Table.Th>
                    <Table.Th>액션</Table.Th>
                  </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                  {contracts.map((item) => (
                    <Table.Tr key={item.id}>
                      <Table.Td style={{ paddingLeft: 20 }}>{item.id}</Table.Td>
                      <Table.Td fw={600}>{item.businessName}</Table.Td>
                      <Table.Td><Badge variant="outline">{item.issueMethod}</Badge></Table.Td>
                      <Table.Td>{new Date(item.requestedAt).toLocaleString()}</Table.Td>
                      <Table.Td>
                        <Button variant="light" size="xs" onClick={() => { setSelectedItem(item); setDetailOpened(true); }}>검토</Button>
                      </Table.Td>
                    </Table.Tr>
                  ))}
                </Table.Tbody>
              </Table>
            </ScrollArea>
          </Card>
        </Tabs.Panel>

        <Tabs.Panel value="events">
          <Card withBorder radius="md" p={0}>
            <ScrollArea>
              <Table verticalSpacing="md" highlightOnHover>
                <Table.Thead bg="gray.0">
                  <Table.Tr>
                    <Table.Th style={{ paddingLeft: 20 }}>ID</Table.Th>
                    <Table.Th>공연 제목</Table.Th>
                    <Table.Th>요청일시</Table.Th>
                    <Table.Th>액션</Table.Th>
                  </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                  {events.map((item) => (
                    <Table.Tr key={item.eventDraftId}>
                      <Table.Td style={{ paddingLeft: 20 }}>{item.eventDraftId}</Table.Td>
                      <Table.Td fw={600}>{item.title}</Table.Td>
                      <Table.Td>{new Date(item.requestedAt).toLocaleString()}</Table.Td>
                      <Table.Td>
                        <Button variant="light" color="teal" size="xs" onClick={() => { setSelectedItem(item); setDetailOpened(true); }}>검토</Button>
                      </Table.Td>
                    </Table.Tr>
                  ))}
                </Table.Tbody>
              </Table>
            </ScrollArea>
          </Card>
        </Tabs.Panel>
      </Tabs>

      <Modal opened={detailOpened} onClose={() => setDetailOpened(false)} title="상세 검토" size="lg" centered>
        {selectedItem && (
          <Stack>
            <SimpleGrid cols={2}>
              <Text size="sm"><b>ID:</b> {selectedItem.id || selectedItem.eventDraftId}</Text>
              <Text size="sm"><b>명칭:</b> {selectedItem.businessName || selectedItem.title}</Text>
            </SimpleGrid>
            <Divider my="xs" label="최종 결정" labelPosition="center" />
            <Group justify="flex-end">
              <Button color="red" variant="outline" onClick={() => setRejectModalOpened(true)}>반려</Button>
              <Button color="green" onClick={handleApprove}>승인 확정</Button>
            </Group>
          </Stack>
        )}
      </Modal>

      <Modal opened={rejectModalOpened} onClose={() => setRejectModalOpened(false)} title="반려 사유 입력">
        <Stack>
          <Textarea 
            label="사유" 
            value={rejectReason} 
            onChange={(e) => setRejectReason(e.currentTarget.value)} 
            placeholder="사유를 입력하세요." 
          />
          <Button fullWidth color="red" onClick={handleReject}>반려 확정</Button>
        </Stack>
      </Modal>
    </Container>
  );
}

export default HqApprovalPage;