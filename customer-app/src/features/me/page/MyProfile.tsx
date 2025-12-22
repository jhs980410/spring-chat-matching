// me/page/MyProfile.tsx
import {
  Box,
  Card,
  Title,
  Text,
  Group,
  Button,
  TextInput,
  Divider,
} from "@mantine/core";

export default function MyProfile() {
  return (
    <Box>
      <Title order={3} mb="lg">
        회원정보 수정
      </Title>

      {/* 기본 정보 */}
      <Card withBorder mb="md">
        <Title order={5} mb="sm">
          기본 정보
        </Title>

        <Group grow>
          <TextInput
            label="이메일"
            value="user1@test.com"
            disabled
          />
          <TextInput
            label="닉네임"
            value="김철수"
            disabled
          />
        </Group>
      </Card>

      {/* 비밀번호 변경 */}
      <Card withBorder mb="md">
        <Title order={5} mb="sm">
          비밀번호 변경
        </Title>

        <TextInput
          label="현재 비밀번호"
          type="password"
          mb="sm"
        />
        <TextInput
          label="새 비밀번호"
          type="password"
          mb="sm"
        />
        <TextInput
          label="새 비밀번호 확인"
          type="password"
        />

        <Group justify="flex-end" mt="md">
          <Button variant="outline">변경</Button>
        </Group>
      </Card>

      {/* 연락처 */}
      <Card withBorder>
        <Title order={5} mb="sm">
          연락처 정보
        </Title>

        <TextInput
          label="휴대폰 번호"
          placeholder="010-0000-0000"
        />

        <Group justify="flex-end" mt="md">
          <Button>저장</Button>
        </Group>
      </Card>

      <Divider my="xl" />

      {/* 회원 탈퇴 */}
      <Card withBorder>
        <Title order={5} c="red" mb="sm">
          회원 탈퇴
        </Title>
        <Text size="sm" c="dimmed">
          회원 탈퇴 시 예매 내역 및 개인정보가 삭제되며 복구할 수 없습니다.
        </Text>

        <Group justify="flex-end" mt="md">
          <Button color="red" variant="outline">
            회원 탈퇴
          </Button>
        </Group>
      </Card>
    </Box>
  );
}
