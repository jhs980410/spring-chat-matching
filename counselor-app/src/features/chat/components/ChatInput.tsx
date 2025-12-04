import { Card, Text } from "@mantine/core";
import type { SessionInfo } from "../../../types"; // 🔥 외부 타입 import (중복 선언 제거)

interface Props {
  session: SessionInfo | null;  // 🔥 null 허용하게 변경
}

export default function ChatUserInfo({ session }: Props) {
  // 🔥 안전장치: session이 아직 null이면 렌더하지 않음
  if (!session) {
    return (
      <Card withBorder shadow="sm" p="md" radius="md">
        <Text size="sm" c="dimmed">
          세션 정보를 불러오는 중...
        </Text>
      </Card>
    );
  }

  return (
    <Card withBorder shadow="sm" p="md" radius="md">
      <Text fw={700}>고객 정보</Text>

      <Text size="sm">이름: {session.userName}</Text>
      <Text size="sm">이메일: {session.userEmail}</Text>

      <Text size="sm" mt="xs">
        도메인: {session.domainName}
      </Text>
      <Text size="sm">카테고리: {session.categoryName}</Text>

      <Text size="sm" mt="xs" c="dimmed">
        요청 시각: {session.requestedAt ?? "-"}
      </Text>
    </Card>
  );
}
