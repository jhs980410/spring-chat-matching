import { Card, Text } from "@mantine/core";
import type { SessionInfo } from "../../../types";

export default function ChatUserInfo({ session }: { session: SessionInfo }) {
  if (!session) return null;

  return (
    <Card withBorder shadow="sm" p="md" radius="md">
      <Text fw={700}>고객 정보</Text>

      <Text size="sm">이름: {session.userName}</Text>
      <Text size="sm">이메일: {session.userEmail}</Text>

      <Text size="sm" mt="xs">도메인: {session.domainName}</Text>
      <Text size="sm">카테고리: {session.categoryName}</Text>

      <Text size="sm" mt="xs" c="dimmed">
        요청 시각: {session.requestedAt ?? "-"}
      </Text>
    </Card>
  );
}
