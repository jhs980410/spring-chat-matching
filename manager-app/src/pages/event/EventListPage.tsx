import { useState, useEffect } from 'react';
import { Container, Title, Card, Table, Badge, Button, Group, Text, ActionIcon } from '@mantine/core';
import { IconEdit, IconEye } from '@tabler/icons-react';
import { managerApi as api } from '../../api/managerApi';
import { useNavigate } from 'react-router-dom';

export function EventListPage() {
  const [drafts, setDrafts] = useState<any[]>([]);
  const nav = useNavigate();

  useEffect(() => {
    // 내가 작성한 초안 목록 가져오기
    api.get('/manager/drafts/my').then(res => setDrafts(res.data));
  }, []);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'APPROVED': return 'green';
      case 'REQUESTED': return 'blue';
      case 'REJECTED': return 'red';
      default: return 'gray';
    }
  };

  return (
    <Container size={1600} fluid py="xl">
      <Group justify="space-between" mb="lg">
        <Title order={2}>상품 등록 내역</Title>
        <Button onClick={() => nav('/events/new')}>신규 상품 등록</Button>
      </Group>

      <Card withBorder radius="md" shadow="sm">
        <Table verticalSpacing="md" highlightOnHover>
          <Table.Thead>
            <Table.Tr>
              <Table.Th>ID</Table.Th>
              <Table.Th>공연명</Table.Th>
              <Table.Th>상태</Table.Th>
              <Table.Th>최종 수정일</Table.Th>
              <Table.Th>관리</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {drafts.map((item) => (
              <Table.Tr key={item.id}>
                <Table.Td>{item.id}</Table.Td>
                <Table.Td><Text fw={500}>{item.title}</Text></Table.Td>
                <Table.Td>
                  <Badge color={getStatusColor(item.status)} variant="light">
                    {item.status}
                  </Badge>
                </Table.Td>
                <Table.Td>{new Date(item.updatedAt).toLocaleDateString()}</Table.Td>
                <Table.Td>
                  <Group gap="xs">
                    <ActionIcon variant="subtle" color="blue"><IconEye size={18} /></ActionIcon>
                    {item.status === 'DRAFT' && (
                      <ActionIcon variant="subtle" color="orange"><IconEdit size={18} /></ActionIcon>
                    )}
                  </Group>
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      </Card>
    </Container>
  );
}