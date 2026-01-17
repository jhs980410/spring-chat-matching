import { useState, useEffect } from 'react';
// Group을 import 목록에 추가했습니다.
import { Container, Title, Card, Badge, Table, Button, LoadingOverlay, Text, Group } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { notifications } from '@mantine/notifications';
import { IconAlertCircle, IconPlus, IconExternalLink } from '@tabler/icons-react';
import axios from 'axios';

const API_BASE = 'http://localhost:8081/api/manager/contracts'; 
const MANAGER_HEADERS = { 'X-MANAGER-ID': '2' };

export function ContractListPage() {
  const nav = useNavigate();
  const [contracts, setContracts] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchMyContracts = async () => {
    setLoading(true);
    try {
      const res = await axios.get(API_BASE, { headers: MANAGER_HEADERS });
      setContracts(res.data);
    } catch (error) {
      console.error('계약 목록 로딩 실패:', error);
      notifications.show({
        title: '데이터 로딩 실패',
        message: '계약 내역을 불러올 수 없습니다.',
        color: 'red',
        icon: <IconAlertCircle size={16} />
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMyContracts();
  }, []);

  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <Container size={1600} fluid py="xl">
      <LoadingOverlay visible={loading} overlayProps={{ blur: 2 }} />
      
      <Title order={2} mb="lg">나의 계약 내역 관리</Title>
      
      <Card withBorder radius="md" shadow="sm">
        <Table verticalSpacing="lg" highlightOnHover>
          <Table.Thead bg="gray.0">
            <Table.Tr>
              <Table.Th>계약 ID</Table.Th>
              <Table.Th>상호 (사업자명)</Table.Th>
              <Table.Th>진행 상태</Table.Th>
              <Table.Th>신청 일시</Table.Th>
              <Table.Th>작업</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {contracts.length > 0 ? (
              contracts.map((c) => (
                <Table.Tr key={c.id}>
                  <Table.Td style={{ width: '100px' }}>#{c.id}</Table.Td>
                  {/* 수정: Table.Td에는 size 속성이 없으므로 내부 Text 컴포넌트에 적용합니다. */}
                  <Table.Td>
                    <Text fw={700} size="md">{c.businessName}</Text>
                  </Table.Td>
                  <Table.Td>
                    <Badge 
                      variant="dot"
                      size="lg"
                      color={
                        c.status === 'APPROVED' ? 'green' : 
                        c.status === 'REQUESTED' ? 'blue' : 
                        c.status === 'REJECTED' ? 'red' : 'gray'
                      }
                    >
                      {c.status}
                    </Badge>
                  </Table.Td>
                  <Table.Td>
                    <Text size="xs" c="dimmed">작성일: {formatDate(c.createdAt)}</Text>
                    <Text size="sm" fw={500}>요청일: {formatDate(c.requestedAt)}</Text>
                  </Table.Td>
                  <Table.Td>
                    {/* 수정: image_abb39c 에러 해결 (Group import 완료) */}
                    <Group gap="xs">
                      <Button 
                        size="xs" 
                        variant="filled"
                        color="blue"
                        leftSection={<IconPlus size={14} />}
                        disabled={c.status !== 'APPROVED'}
                        onClick={() => nav(`/events/new?contractId=${c.id}`)}
                      >
                        상품 등록
                      </Button>
                      <Button 
                        size="xs" 
                        variant="light" 
                        color="gray"
                        leftSection={<IconExternalLink size={14} />}
                      >
                        상세보기
                      </Button>
                    </Group>
                  </Table.Td>
                </Table.Tr>
              ))
            ) : (
              <Table.Tr>
                <Table.Td colSpan={5} style={{ textAlign: 'center', padding: '60px' }}>
                  <Text c="dimmed">신청된 판매 계약이 없습니다.</Text>
                </Table.Td>
              </Table.Tr>
            )}
          </Table.Tbody>
        </Table>
      </Card>
    </Container>
  );
}

export default ContractListPage;