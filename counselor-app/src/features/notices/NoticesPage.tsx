import { Card, Title, Table, Text, Badge, Group } from "@mantine/core";
import { Link } from "react-router-dom";

// 🔹 하드코딩 공지 데이터
const notices = [
  {
    id: 1,
    title: "📢 시스템 점검 안내",
    author: "관리자",
    created_at: "2024-12-10 10:00",
    summary: "12월 12일 새벽 2시~4시 시스템 점검이 진행됩니다.",
  },
  {
    id: 2,
    title: "🎉 상담 서비스 신규 기능 업데이트",
    author: "운영팀",
    created_at: "2024-12-09 14:30",
    summary: "상담사 대시보드 및 통계 기능이 업데이트되었습니다.",
  },
  {
    id: 3,
    title: "📄 개인정보 처리 방침 변경",
    author: "보안팀",
    created_at: "2024-12-05 09:00",
    summary: "개인정보 처리방침이 일부 변경되어 안내드립니다.",
  },
];

export default function NoticesPage() {
  return (
    <Card p="lg" withBorder radius="md">
      <Title order={3} mb="md">
        공지사항
      </Title>

      <Table striped highlightOnHover>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>ID</Table.Th>
            <Table.Th>제목</Table.Th>
            <Table.Th>작성자</Table.Th>
            <Table.Th>작성일</Table.Th>
          </Table.Tr>
        </Table.Thead>

        <Table.Tbody>
          {notices.map((n) => (
            <Table.Tr key={n.id}>
              <Table.Td>{n.id}</Table.Td>

              <Table.Td>
                <Group gap="xs">
                  <Badge color="blue">공지</Badge>
                  <Link
                    to={`/notices/${n.id}`}
                    style={{ textDecoration: "none", color: "black" }}
                  >
                    <b>{n.title}</b>
                    <Text size="xs" c="dimmed">
                      {n.summary}
                    </Text>
                  </Link>
                </Group>
              </Table.Td>

              <Table.Td>{n.author}</Table.Td>

              <Table.Td>{n.created_at}</Table.Td>
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </Card>
  );
}
