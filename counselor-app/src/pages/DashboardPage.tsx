import { useParams } from "react-router-dom";
import { Title, Card, Text } from "@mantine/core";
import DashboardLayout from "../layouts/DashboardLayout";

export default function ChatPage() {
  const { sessionId } = useParams();

  return (
    <DashboardLayout>
      <Title order={2} mb="md">
        채팅 세션 #{sessionId}
      </Title>

      <Card shadow="sm" padding="lg" radius="md" withBorder>
        <Text>여기에 WebSocket 기반 채팅 UI 표시</Text>
      </Card>
    </DashboardLayout>
  );
}
