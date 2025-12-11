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

  const ws = useWS();

  const [session, setSession] = useState<SessionInfo | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // ============================================================
  // 1) sessionId가 바뀔 때, 기존 데이터 초기화
  // ============================================================
  useEffect(() => {
    setSession(null);
    setMessages([]);
    setLoading(true);
    setError("");
  }, [sid]);

  // ============================================================
  // 2) HTTP API로 초기 세션 정보 + 기존 메시지 로드
  // ============================================================
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

    fetchData();
  }, [sid]);

  // ============================================================
  // 3) WebSocket 실시간 메시지 구독
  // ============================================================
  useEffect(() => {
    if (!ws) {
      console.log("[WS] 아직 연결되지 않음");
      return;
    }
    if (!session) return;

    const topic = `/sub/session/${sid}`;
    console.log("[WS] SUBSCRIBE:", topic);

    const subscription = ws.subscribe(topic, (msg) => {
      try {
        const data = JSON.parse(msg.body);
        console.log("[WS] RECEIVE:", data);

        setMessages((prev) => [...prev, data]);
      } catch (err) {
        console.error("[WS] JSON Parse Error:", err);
      }
    });

    return () => {
  try {
    subscription?.unsubscribe();
    console.log("[WS] UNSUBSCRIBE:", topic);
  } catch (e) {
    console.warn("[WS] unsubscribe 실패:", e);
  }
};
  }, [ws, session, sid]);

  // ============================================================
  // 4) 로딩 상태
  // ============================================================
  if (loading) {
    return (
      <Center mt="xl">
        <Loader />
      </Center>
    );
  }

  // ============================================================
  // 5) 에러 또는 세션 없음
  // ============================================================
  if (error || !session) {
    return (
      <Title order={2} c="red">
        {error || "존재하지 않는 세션입니다."}
      </Title>
    );
  }

  // ============================================================
  // 6) UI 렌더링
  // ============================================================
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
          <Card withBorder shadow="sm" p="md" radius="md">
            <ChatHeader session={session} />

            {/* 🔥 실시간 메시지 표시 */}
            <ChatWindow messages={messages} />

            {/* 🔥 메시지 전송 시 UI 업데이트 setMessages 전달 */}
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
