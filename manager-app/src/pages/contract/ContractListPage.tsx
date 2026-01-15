import { Container, Title, Card, Badge, Table, Button, Text } from '@mantine/core';
import { useNavigate } from 'react-router-dom';

export function ContractListPage() {
  const nav = useNavigate();
  // 더미 데이터 (실제로는 API 호출)
  const contracts = [
    { id: 1001, title: '더원 콘서트 판매 계약', vendor: 'NOL기획', status: 'APPROVED' },
    { id: 1002, title: '뮤지컬 위키드 계약', vendor: 'NOL기획', status: 'PENDING' },
  ];

  return (
    <Container size={1600} fluid py="xl">
      <Title order={2} mb="lg">계약 내역 조회</Title>
      <Card withBorder radius="md">
        <Table verticalSpacing="lg">
          <Table.Thead>
            <Table.Tr>
              <Table.Th>계약 ID</Table.Th>
              <Table.Th>계약명</Table.Th>
              <Table.Th>파트너사</Table.Th>
              <Table.Th>상태</Table.Th>
              <Table.Th>액션</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {contracts.map((c) => (
              <Table.Tr key={c.id}>
                <Table.Td>{c.id}</Table.Td>
                <Table.Td fw={600}>{c.title}</Table.Td>
                <Table.Td>{c.vendor}</Table.Td>
                <Table.Td>
                  <Badge color={c.status === 'APPROVED' ? 'green' : 'orange'}>{c.status}</Badge>
                </Table.Td>
                <Table.Td>
                  <Button 
                    size="xs" 
                    variant="light" 
                    disabled={c.status !== 'APPROVED'}
                    onClick={() => nav('/events/new')}
                  >
                    상품 등록하기
                  </Button>
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      </Card>
    </Container>
  );
}