import { useState } from "react";
import {
  Card,
  Title,
  Text,
  TextInput,
  PasswordInput,
  Button,
  Group,
  Badge,
  Table,
} from "@mantine/core";

import CounselorLayout from "../../layouts/CounselorLayout";
import { mockCounselors } from "../../data/mock/mockCounselors";

const loginHistory = [
  { id: 1, device: "Windows Chrome", ip: "123.111.55.20", time: "2024-12-10 14:22" },
  { id: 2, device: "iPhone Safari", ip: "123.111.55.20", time: "2024-12-08 08:14" },
  { id: 3, device: "MacBook Chrome", ip: "123.111.55.20", time: "2024-12-06 16:42" },
];

export default function ProfilePage() {
  const counselor = mockCounselors.find((c) => c.id === 1);

  const [form, setForm] = useState({
    oldPassword: "",
    newPassword: "",
    newPasswordConfirm: "",
  });

  const handlePasswordChange = () => {
    if (form.newPassword !== form.newPasswordConfirm) {
      alert("새 비밀번호가 일치하지 않습니다!");
      return;
    }
    alert("비밀번호 변경 (목업)");
  };

  return (
 
      <Card p="lg" withBorder radius="md">
        <Title order={2} mb="lg">내 정보</Title>

        {/* 상담사 정보 */}
        <Card withBorder p="md" mb="lg">
          <Title order={4} mb="sm">기본 정보</Title>

          <Text size="sm">이름: {counselor?.name}</Text>
          <Text size="sm">이메일: {counselor?.email}</Text>

          <Group mt="xs">
            <Text size="sm">상태:</Text>
            <Badge>{counselor?.status}</Badge>
          </Group>

          <Text size="sm" mt="xs">
            현재 처리 건수(load): {counselor?.current_load ?? 0}
          </Text>
          <Text size="sm">등록일: {counselor?.created_at}</Text>
        </Card>

        {/* 로그인 기록 */}
        <Card withBorder p="md" mb="lg">
          <Title order={4} mb="sm">최근 로그인 기록</Title>

          <Table striped>
            <Table.Thead>
              <Table.Tr>
                <Table.Th>기기</Table.Th>
                <Table.Th>IP</Table.Th>
                <Table.Th>시간</Table.Th>
              </Table.Tr>
            </Table.Thead>

            <Table.Tbody>
              {loginHistory.map((log) => (
                <Table.Tr key={log.id}>
                  <Table.Td>{log.device}</Table.Td>
                  <Table.Td>{log.ip}</Table.Td>
                  <Table.Td>{log.time}</Table.Td>
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        </Card>

        {/* 비밀번호 변경 */}
        <Card withBorder p="md">
          <Title order={4} mb="sm">비밀번호 변경</Title>

          <TextInput
            label="현재 비밀번호"
            type="password"
            value={form.oldPassword}
            onChange={(e) =>
              setForm((f) => ({ ...f, oldPassword: e.target.value }))
            }
            mb="sm"
          />

          <PasswordInput
            label="새 비밀번호"
            value={form.newPassword}
            onChange={(e) =>
              setForm((f) => ({ ...f, newPassword: e.target.value }))
            }
            mb="sm"
          />

          <PasswordInput
            label="새 비밀번호 확인"
            value={form.newPasswordConfirm}
            onChange={(e) =>
              setForm((f) => ({ ...f, newPasswordConfirm: e.target.value }))
            }
          />

          <Button fullWidth mt="lg" onClick={handlePasswordChange}>
            비밀번호 변경 (목업)
          </Button>
        </Card>
      </Card>

  );
}
