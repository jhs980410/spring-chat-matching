import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Grid, Title, Card, Loader, Center } from "@mantine/core";
import axios from "axios";

import ChatUserInfo from "../components/ChatUserInfo";
import ChatHeader from "../components/ChatHeader";
import ChatWindow from "../components/ChatWindow";
import ChatInput from "../components/ChatInput";
import ChatStatusPanel from "../components/ChatStatusPanel";

import type { SessionInfo, ChatMessage } from "../../../types/index";

export default function ChatPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const sid = Number(sessionId);

  const [session, setSession] = useState<SessionInfo | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // ================================
  // API 요청
  // ================================
  useEffect(() => {
    const fetchData = async () => {
      try {
        const sessionRes = await axios.get(`/api/sessions/${sid}/detail`, {
          withCredentials: true,
        });
        setSession(sessionRes.data);

        const msgRes = await axios.get(`/api/messages/${sid}`, {
          withCredentials: true,
        });
        setMessages(msgRes.data);
      } catch (e) {
        setError("세션 정보를 불러오지 못했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [sid]);

  // ================================
  // 로딩 처리
  // ================================
  if (loading) {
    return (
      <Center mt="xl">
        <Loader />
      </Center>
    );
  }

  // ================================
  // 에러 처리
  // ================================
  if (error || !session) {
    return (
      <Title order={2} c="red">
        {error || "존재하지 않는 세션입니다."}
      </Title>
    );
  }

  // ================================
  // UI 렌더링
  // ================================
  return (
    <>
      <Title order={2} mb="md">
        상담 세션 #{sid}
      </Title>

      <Grid gutter="xl">
        {/* LEFT (사용자 정보) */}
        <Grid.Col span={3}>
          <ChatUserInfo session={session} />
        </Grid.Col>

        {/* CENTER (실제 채팅) */}
        <Grid.Col span={6}>
          <Card withBorder shadow="sm" p="md" radius="md">
            <ChatHeader session={session} />
            <ChatWindow messages={messages} />
            <ChatInput sessionId={sid} />
          </Card>
        </Grid.Col>

        {/* RIGHT (상담 상태 및 AfterCall) */}
        <Grid.Col span={3}>
          <ChatStatusPanel session={session} />
        </Grid.Col>
      </Grid>
    </>
  );
}
