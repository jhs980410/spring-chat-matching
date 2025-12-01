import { useParams } from "react-router-dom";
import { Grid, Title, Card } from "@mantine/core";

// Mock 데이터
import { mockSessions } from "../../../data/mock/mockSessions";
import { mockMessages } from "../../../data/mock/mockMessages";

// 분리된 컴포넌트
import ChatUserInfo from "../components/ChatUserInfo";
import ChatHeader from "../components/ChatHeader";
import ChatWindow from "../components/ChatWindow";
import ChatInput from "../components/ChatInput";
import ChatStatusPanel from "../components/ChatStatusPanel";

export default function ChatPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const sid = Number(sessionId);

  const session = mockSessions.find((s) => s.id === sid);

  if (!session) {
    return <Title order={2}>존재하지 않는 세션입니다.</Title>;
  }

  const messages = mockMessages.filter((m) => m.session_id === sid);

  return (
    <>
      <Title order={2} mb="md">
        채팅 세션 #{sid}
      </Title>

      <Grid gutter="xl">
        {/* 좌측 고객 정보 */}
        <Grid.Col span={3}>
          <ChatUserInfo session={session} />
        </Grid.Col>

        {/* 중앙 채팅 UI */}
        <Grid.Col span={6}>
          <Card withBorder shadow="sm" p="md" radius="md">
            <ChatHeader session={session} />

            <ChatWindow messages={messages} />

            <ChatInput />
          </Card>
        </Grid.Col>

        {/* 우측 상담 상태 + After Call */}
        <Grid.Col span={3}>
          <ChatStatusPanel session={session} />
        </Grid.Col>
      </Grid>
    </>
  );
}
