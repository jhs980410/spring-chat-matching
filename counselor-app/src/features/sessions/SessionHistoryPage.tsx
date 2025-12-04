import { useState, useMemo, useEffect } from "react";
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

import axios from "axios";
import { Link } from "react-router-dom";

interface SessionHistoryItem {
  sessionId: number;
  userName: string;
  userEmail: string;
  counselorName?: string;
  categoryName: string;
  status: string;
  requestedAt: string | null;   // ← null 가능!
}

export default function SessionHistoryPage() {
  const [sessions, setSessions] = useState<SessionHistoryItem[]>([]);
  const [loading, setLoading] = useState(true);

  const [emailFilter, setEmailFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("");

  useEffect(() => {
    axios
      .get("/api/sessions/history")
      .then((res) => setSessions(res.data))
      .finally(() => setLoading(false));
  }, []);

  const rows = useMemo(() => {
    return sessions
      .filter((s) => {
        const matchEmail = emailFilter
          ? s.userEmail.toLowerCase().includes(emailFilter.toLowerCase())
          : true;

        const matchStatus = statusFilter ? s.status === statusFilter : true;

        const matchCategory = categoryFilter
          ? s.categoryName === categoryFilter
          : true;

        return matchEmail && matchStatus && matchCategory;
      })
      .map((s) => (
        <Table.Tr key={s.sessionId}>
          <Table.Td>{s.sessionId}</Table.Td>
          <Table.Td>{s.userName}</Table.Td>
          <Table.Td>{s.counselorName ?? "-"}</Table.Td>

          <Table.Td>
            <Badge
              color={
                s.status === "IN_PROGRESS"
                  ? "blue"
                  : s.status === "ENDED"
                  ? "green"
                  : s.status === "AFTER_CALL"
                  ? "yellow"
                  : "gray"
              }
            >
              {s.status}
            </Badge>
          </Table.Td>

          <Table.Td>{s.categoryName}</Table.Td>

          {/* 🔥 요청 시간 substring null-safe 적용 */}
          <Table.Td>
            {s.requestedAt ? s.requestedAt.substring(0, 16) : "-"}
          </Table.Td>

          <Table.Td>
            <Button
              component={Link}
              to={`/sessions/${s.sessionId}`}
              size="xs"
              variant="light"
            >
              상세보기
            </Button>
          </Table.Td>
        </Table.Tr>
      ));
  }, [sessions, emailFilter, statusFilter, categoryFilter]);

  if (loading) return <div>로딩 중...</div>;

  return (
    <Card withBorder radius="md" p="lg">
      <Title order={3} mb="md">
        상담 내역 조회
      </Title>

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
          data={[...new Set(sessions.map((s) => s.categoryName))].map(
            (name) => ({
              value: name,
              label: name,
            })
          )}
          value={categoryFilter}
          onChange={setCategoryFilter}
          clearable
        />
      </Group>

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
