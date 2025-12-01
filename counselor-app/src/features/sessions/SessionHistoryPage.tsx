import { useState, useMemo } from "react";
import {
  Card,
  Title,
  TextInput,
  Select,
  Table,
  Badge,
  Group,
  Button,
} from "@mantine/core";

import { mockSessions } from "../../data/mock/mockSessions";
import { mockUsers } from "../../data/mock/mockUsers";
import { mockCounselors } from "../../data/mock/mockCounselors";
import { mockCategories } from "../../data/mock/mockCategories";

import { Link } from "react-router-dom";

export default function SessionHistoryPage() {
  const [emailFilter, setEmailFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("");

  const rows = useMemo(() => {
    return mockSessions
      .filter((s) => {
        const user = mockUsers.find((u) => u.id === s.user_id);
        if (!user) return false;

        const matchEmail = emailFilter
          ? user.email.toLowerCase().includes(emailFilter.toLowerCase())
          : true;

        const matchStatus = statusFilter ? s.status === statusFilter : true;

        const matchCategory = categoryFilter
          ? String(s.category_id) === categoryFilter
          : true;

        return matchEmail && matchStatus && matchCategory;
      })
      .map((s) => {
        const user = mockUsers.find((u) => u.id === s.user_id);
        const counselor = mockCounselors.find((c) => c.id === s.counselor_id);
        const category = mockCategories.find((cat) => cat.id === s.category_id);

        return (
          <Table.Tr key={s.id}>
            <Table.Td>{s.id}</Table.Td>
            <Table.Td>{user?.nickname}</Table.Td>
            <Table.Td>{counselor?.name ?? "-"}</Table.Td>
            <Table.Td>
              <Badge
                color={
                  s.status === "IN_PROGRESS"
                    ? "blue"
                    : s.status === "ENDED"
                    ? "green"
                    : "gray"
                }
              >
                {s.status}
              </Badge>
            </Table.Td>
            <Table.Td>{category?.name}</Table.Td>
            <Table.Td>{s.requested_at.substring(0, 16)}</Table.Td>
            <Table.Td>
              <Button
                component={Link}
                to={`/sessions/${s.id}`}
                size="xs"
                variant="light"
              >
                상세보기
              </Button>
            </Table.Td>
          </Table.Tr>
        );
      });
  }, [emailFilter, statusFilter, categoryFilter]);

  return (
    <Card withBorder radius="md" p="lg">
      <Title order={3} mb="md">
        상담 내역 조회
      </Title>

      {/* 필터 */}
      <Group grow mb="md">
        <TextInput
          label="이메일 검색"
          placeholder="example@test.com"
          value={emailFilter}
          onChange={(e) => setEmailFilter(e.currentTarget.value)}
        />

        <Select
          label="상태"
          placeholder="전체"
          data={[
            { value: "WAITING", label: "대기" },
            { value: "IN_PROGRESS", label: "진행중" },
            { value: "AFTER_CALL", label: "후처리" },
            { value: "ENDED", label: "종료" },
          ]}
          value={statusFilter}
          onChange={setStatusFilter}
          clearable
        />

        <Select
          label="카테고리"
          placeholder="전체"
          data={mockCategories.map((c) => ({
            value: String(c.id),
            label: c.name,
          }))}
          value={categoryFilter}
          onChange={setCategoryFilter}
          clearable
        />
      </Group>

      {/* 테이블 */}
      <Table striped highlightOnHover>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>ID</Table.Th>
            <Table.Th>고객</Table.Th>
            <Table.Th>상담사</Table.Th>
            <Table.Th>상태</Table.Th>
            <Table.Th>카테고리</Table.Th>
            <Table.Th>요청시각</Table.Th>
            <Table.Th></Table.Th>
          </Table.Tr>
        </Table.Thead>

        <Table.Tbody>{rows}</Table.Tbody>
      </Table>
    </Card>
  );
}
