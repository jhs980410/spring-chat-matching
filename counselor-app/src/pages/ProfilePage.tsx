import { Card, Title, TextInput, Button, Group } from "@mantine/core";
import CounselorLayout from "../layouts/CounselorLayout";
import { useState } from "react";

export default function ProfilePage() {
  const [name, setName] = useState("홍길동");
  const [email, setEmail] = useState("counselor@test.com");

  return (
    <CounselorLayout>
      <Title order={2} mb="md">내 정보 관리</Title>

      <Card withBorder shadow="sm" p="lg" w={400}>
        <TextInput
          label="이름"
          value={name}
          onChange={(e) => setName(e.currentTarget.value)}
          mb="md"
        />

        <TextInput
          label="이메일"
          value={email}
          onChange={(e) => setEmail(e.currentTarget.value)}
          disabled
          mb="md"
        />

        <Group>
          <Button variant="light" color="orange">
            비밀번호 변경
          </Button>

          <Button color="blue">
            저장
          </Button>
        </Group>
      </Card>
    </CounselorLayout>
  );
}
