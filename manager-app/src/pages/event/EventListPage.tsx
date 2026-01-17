import { useState, useEffect } from 'react';
import { 
  Table, Badge, Group, Text, Button, Card, 
  Title, Container, Stack, ActionIcon, Tooltip, Box, Loader, Center
} from '@mantine/core';
import { notifications } from '@mantine/notifications';
import { IconSend, IconSearch, IconPlus, IconCircleCheck } from '@tabler/icons-react';
import { managerApi as api } from '../../api/managerApi';
import { useNavigate } from 'react-router-dom';

export function EventListPage() {
  const [drafts, setDrafts] = useState([]);
  const [loading, setLoading] = useState(true);
  const nav = useNavigate();

  // 1. 목록 조회 (상태 파라미터 없이 전체 조회하여 Enum 에러 방지)
  const fetchDrafts = async () => {
    try {
      setLoading(true);
      // status 쿼리 파라미터를 제거하여 InvalidDataAccessApiUsageException 방지
      const response = await api.get('/manager/drafts', {
        headers: { 'X-MANAGER-ID': '2' } 
      });
      setDrafts(response.data);
    } catch (error) {
      notifications.show({
        title: '조회 실패',
        message: '초안 목록을 불러오는 중 오류가 발생했습니다.',
        color: 'red'
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchDrafts(); }, []);

  // 2. 승인 요청 처리 (DRAFT -> REQUESTED)
  const handleRequestApproval = async (id: number) => {
    if (!confirm("본사에 승인을 요청하시겠습니까? 요청 후에는 수정이 불가능합니다.")) return;
    
    try {
      await api.post(`/manager/drafts/${id}/request`, null, {
        headers: { 'X-MANAGER-ID': '2' }
      });
      notifications.show({
        title: '요청 성공',
        message: '본사에 승인 요청이 전달되었습니다.',
        color: 'blue'
      });
      fetchDrafts(); // 요청 후 상태 갱신을 위해 목록 새로고침
    } catch (error) {
      notifications.show({
        title: '요청 실패',
        message: '이미 요청된 상태이거나 권한이 없습니다.',
        color: 'red'
      });
    }
  };

  // 상태별 배지 설정 함수
  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'DRAFT': return <Badge color="gray" variant="light">작성 중</Badge>;
      case 'REQUESTED': return <Badge color="orange" variant="filled">승인 대기</Badge>;
      case 'APPROVED': return <Badge color="green" variant="filled" leftSection={<IconCircleCheck size={12}/>}>승인 완료</Badge>;
      case 'REJECTED': return <Badge color="red" variant="outline">반려됨</Badge>;
      default: return <Badge color="dark">{status}</Badge>;
    }
  };

  return (
    <Container size={1400} py="xl">
      <Stack gap="lg">
        <Group justify="space-between">
          <Box>
            <Title order={2} c="blue.9" style={{ letterSpacing: '-1px' }}>공연 등록 및 승인 내역</Title>
            <Text size="sm" c="dimmed">
              등록한 공연의 상태를 확인하세요. <b>작성 중</b> 상태에서 <b>승인 요청</b>을 해야 본사 검토가 시작됩니다.
            </Text>
          </Box>
          <Button 
            leftSection={<IconPlus size={18}/>} 
            onClick={() => nav('/events/new')}
            variant="filled"
          >
            신규 공연 등록
          </Button>
        </Group>

        <Card withBorder radius="md" shadow="sm" p={0}>
          <Table verticalSpacing="md" highlightOnHover>
            <Table.Thead bg="gray.0">
              <Table.Tr>
                <Table.Th style={{ width: 80, paddingLeft: 20 }}>ID</Table.Th>
                <Table.Th>공연 제목</Table.Th>
                <Table.Th>공연 장소</Table.Th>
                <Table.Th>진행 상태</Table.Th>
                <Table.Th>최종 업데이트</Table.Th>
                <Table.Th style={{ width: 180 }}>액션</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {loading ? (
                <Table.Tr>
                  <Table.Td colSpan={6}>
                    <Center py="xl"><Loader size="md" type="dots" /></Center>
                  </Table.Td>
                </Table.Tr>
              ) : drafts.length > 0 ? drafts.map((item: any) => (
                <Table.Tr key={item.id}>
                  <Table.Td style={{ paddingLeft: 20 }}>{item.id}</Table.Td>
                  <Table.Td><Text fw={600} size="sm">{item.title}</Text></Table.Td>
                  <Table.Td><Text size="sm">{item.venue}</Text></Table.Td>
                  <Table.Td>{getStatusBadge(item.status)}</Table.Td>
                  <Table.Td>
                    <Text size="xs" c="dimmed">
                      {item.requestedAt ? new Date(item.requestedAt).toLocaleString() : '미요청'}
                    </Text>
                  </Table.Td>
                  <Table.Td>
                    <Group gap={8}>
                      <Tooltip label="상세보기">
                        <ActionIcon 
                          variant="light" 
                          color="blue" 
                          onClick={() => nav(`/events/${item.id}`)}
                          size="lg"
                        >
                          <IconSearch size={18} />
                        </ActionIcon>
                      </Tooltip>
                      
                      {/* 💡 핵심: DRAFT 상태인 경우에만 '본사 승인 요청' 버튼을 활성화 */}
                      {item.status === 'DRAFT' ? (
                        <Button
                          size="xs"
                          color="teal"
                          leftSection={<IconSend size={14} />}
                          onClick={() => handleRequestApproval(item.id)}
                        >
                          승인 요청
                        </Button>
                      ) : (
                        <Text size="xs" c="dimmed" fw={500}>
                          {item.status === 'REQUESTED' ? '검토 진행 중' : '조작 불가'}
                        </Text>
                      )}
                    </Group>
                  </Table.Td>
                </Table.Tr>
              )) : (
                <Table.Tr>
                  <Table.Td colSpan={6} ta="center" py="50">
                    <Stack gap="xs">
                      <Text c="dimmed" fw={500}>등록된 공연 초안이 없습니다.</Text>
                      <Center>
                        <Button variant="subtle" size="xs" onClick={() => nav('/events/new')}>첫 공연 등록하기</Button>
                      </Center>
                    </Stack>
                  </Table.Td>
                </Table.Tr>
              )}
            </Table.Tbody>
          </Table>
        </Card>
      </Stack>
    </Container>
  );
}