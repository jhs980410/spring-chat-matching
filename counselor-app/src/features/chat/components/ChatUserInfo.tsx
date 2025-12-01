import { Card, Text } from "@mantine/core";

export default function ChatUserInfo({ session }: any) {
  return (
    <Card withBorder shadow="sm" p="md" radius="md">
      <Text fw={700}>고객 정보</Text>

      <Text size="sm">이름: {session.user_name}</Text>
      <Text size="sm">이메일: {session.user_email}</Text>

      <Text size="sm" mt="xs">도메인: {session.domain_name}</Text>
      <Text size="sm">카테고리: {session.category_name}</Text>

      <Text size="sm" mt="xs" c="dimmed">
        요청 시각: {session.requested_at}
      </Text>
    </Card>
  );
}
