import { useState, useEffect } from 'react';
// Group, Container 등 Mantine 컴포넌트 import
import { Container, Title, Card, Badge, Table, Button, LoadingOverlay, Text, Group } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { notifications } from '@mantine/notifications';
import { IconAlertCircle, IconPlus, IconExternalLink } from '@tabler/icons-react';
import axios from 'axios';

/**
 * ❗ [핵심 수정] 하드코딩된 localhost 주소를 제거했습니다.
 * 브라우저는 이제 현재 도메인의 /api/manager/contracts로 요청을 보냅니다.
 */
const API_BASE = '/api/manager/contracts'; 
const MANAGER_HEADERS = { 'X-MANAGER-ID': '2' };

export function ContractListPage() {
  const nav = useNavigate();
  const [contracts, setContracts] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  // 계약 목록 조회 함수
  const fetchMyContracts = async () => {
    setLoading(true);
    try {
      /**
       * Nginx 설정(proxy_pass http://ticket-manager-service:8081)에 의해
       * 이 요청은 내부 도커 네트워크의 8081 백엔드로 전달됩니다.
       */
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

  // 날짜 포맷팅 유틸리티
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