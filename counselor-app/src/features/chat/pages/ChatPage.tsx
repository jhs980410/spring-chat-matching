// ChatPage.tsx
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Grid, Title, Card, Loader, Center } from "@mantine/core";

import api from "../../../api/axios";

import ChatUserInfo from "../components/ChatUserInfo";
import ChatHeader from "../components/ChatHeader";
import ChatWindow from "../components/ChatWindow";
import ChatInput from "../components/ChatInput";
import ChatStatusPanel from "../components/ChatStatusPanel";

import { useWS } from "../../providers/useWS";
import type { SessionInfo, ChatMessage } from "../../../types";

export default function ChatPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const sid = Number(sessionId);

  const { client, connected } = useWS();

  const [session, setSession] = useState<SessionInfo | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // =====================================
  // 1️⃣ sessionId 변경 시 상태 초기화
  // =====================================
  useEffect(() => {
    setSession(null);
    setMessages([]);
    setLoading(true);
    setError(null);
  }, [sid]);

  // =====================================
  // 2️⃣ 초기 세션 + 메시지 로드
  // =====================================
  useEffect(() => {
    const fetchData = async () => {
      try {
        const sessionRes = await api.get(`/sessions/${sid}/detail`, {
          withCredentials: true,
        });
        setSession(sessionRes.data);

        const msgRes = await api.get(`/messages/${sid}`, {
          withCredentials: true,
        });
        setMessages(msgRes.data);
      } catch (e) {
        setError("세션 정보를 불러오지 못했습니다.");
      } finally {
        setLoading(false);
      }
    };

    if (!Number.isNaN(sid)) {
      fetchData();
    }
  }, [sid]);

  // =====================================
  // 3️⃣ WebSocket 실시간 메시지 구독
  // =====================================
  useEffect(() => {
    if (!connected || !client || !session) return;

    const topic = `/sub/session/${sid}`;
    console.log("[WS] SUBSCRIBE:", topic);

    const subscription = client.subscribe(topic, (msg) => {
      try {
        const data: ChatMessage = JSON.parse(msg.body);
        setMessages((prev) => [...prev, data]);
      } catch (e) {
        console.error("[WS] message parse error", e);
      }
    });

    return () => {
      console.log("[WS] UNSUBSCRIBE:", topic);
      subscription.unsubscribe();
    };
  }, [connected, client, session, sid]);

  // =====================================
  // 4️⃣ 로딩 / 에러 처리
  // =====================================
  if (loading) {
    return (
      <Center mt="xl">
        <Loader />
      </Center>
    );
  }

  if (error || !session) {
    return (
      <Title order={2} c="red">
        {error ?? "존재하지 않는 세션입니다."}
      </Title>
    );
  }

  // =====================================
  // 5️⃣ UI
  // =====================================
  return (
    <>
      <Title order={2} mb="md">
        상담 세션 #{sid}
      </Title>

      <Grid gutter="xl">
        <Grid.Col span={3}>
          <ChatUserInfo session={session} />
        </Grid.Col>

        <Grid.Col span={6}>
          <Card withBorder shadow="sm" p="md">
            <ChatHeader session={session} />
            <ChatWindow messages={messages} />
            <ChatInput sessionId={sid} onNewMessage={setMessages} />
          </Card>
        </Grid.Col>

        <Grid.Col span={3}>
          <ChatStatusPanel session={session} />
        </Grid.Col>
      </Grid>
    </>
  );
}
