import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  Card,
  Title,
  Text,
  Divider,
  Group,
  ScrollArea,
  Badge,
} from "@mantine/core";

import axios from "axios";

interface MessageItem {
  id: number;
  senderType: "USER" | "COUNSELOR";
  senderName: string;
  message: string;
  createdAt: string;
}

interface AfterCallLog {
  satisfactionScore?: number;
  afterCallSec?: number;
  feedback?: string;
  endedAt?: string;
}

interface SessionDetail {
  sessionId: number;
  status: string;

  userId: number;
  userName: string;
  userEmail: string;

  counselorId?: number;
  counselorName?: string;

  domainName: string;
  categoryName: string;

  requestedAt: string;
  assignedAt?: string;
  startedAt?: string;
  endedAt?: string;
  durationSec?: number;

  messages: MessageItem[];
  afterCall?: AfterCallLog;
}

export default function SessionDetailPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const [session, setSession] = useState<SessionDetail | null>(null);
  const [loading, setLoading] = useState(true);

  // ============================
  // 🔥 실제 API 호출
  // ============================
  useEffect(() => {
    axios
      .get(`/api/sessions/${sessionId}/detail`)
      .then((res) => setSession(res.data))
      .finally(() => setLoading(false));
  }, [sessionId]);

  if (loading) return <div>로딩중...</div>;

  if (!session) {
    return (
      <Card p="lg" withBorder>
        <Title order={3}>세션이 존재하지 않습니다.</Title>
      </Card>
    );
  }

  return (
    <Card p="lg" withBorder radius="md">
      <Title order={3} mb="md">
        상담 상세 정보 #{session.sessionId}
      </Title>

      {/* =================== */}
      {/* 고객 정보 & 상담사 정보 */}
      {/* =================== */}
      <Group align="flex-start" grow mb="xl">
        <Card withBorder radius="md" p="md">
          <Title order={5} mb="sm">고객 정보</Title>
          <Text size="sm">이름: {session.userName}</Text>
          <Text size="sm">이메일: {session.userEmail}</Text>
        </Card>

        <Card withBorder radius="md" p="md">
          <Title order={5} mb="sm">상담사 정보</Title>
          <Text size="sm">이름: {session.counselorName ?? "-"}</Text>
          <Text size="sm">ID: {session.counselorId ?? "-"}</Text>
        </Card>
      </Group>

      {/* =================== */}
      {/* 세션 정보 */}
      {/* =================== */}
      <Card withBorder radius="md" p="md" mb="xl">
        <Title order={5} mb="sm">세션 정보</Title>

        <Group grow>
          <div>
            <Text size="sm">
              도메인: <b>{session.domainName}</b>
            </Text>
            <Text size="sm">
              카테고리: <b>{session.categoryName}</b>
            </Text>
            <Text size="sm">
              상태:{" "}
              <Badge
                color={
                  session.status === "IN_PROGRESS"
                    ? "blue"
                    : session.status === "ENDED"
                    ? "green"
                    : session.status === "AFTER_CALL"
                    ? "yellow"
                    : "gray"
                }
              >
                {session.status}
              </Badge>
            </Text>
          </div>

          <div>
            <Text size="sm">요청: {session.requestedAt}</Text>
            <Text size="sm">배정: {session.assignedAt ?? "-"}</Text>
            <Text size="sm">시작: {session.startedAt ?? "-"}</Text>
            <Text size="sm">종료: {session.endedAt ?? "-"}</Text>
            <Text size="sm">
              상담 시간: {session.durationSec ? `${session.durationSec}s` : "-"}
            </Text>
          </div>
        </Group>
      </Card>

      {/* =================== */}
      {/* 메시지 타임라인 */}
      {/* =================== */}
      <Title order={5} mb="sm">메시지 기록</Title>

      <ScrollArea h={300} scrollbarSize={6} mb="xl">
        <Card withBorder radius="md" p="md">
          <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
            {session.messages.map((msg) => (
              <div
                key={msg.id}
                style={{
                  alignSelf:
                    msg.senderType === "COUNSELOR" ? "flex-end" : "flex-start",
                  backgroundColor:
                    msg.senderType === "COUNSELOR" ? "#d0ebff" : "#f1f3f5",
                  padding: "8px 12px",
                  borderRadius: 10,
                  maxWidth: "75%",
                }}
              >
                <Text size="sm" fw={500}>
                  {msg.senderName}
                </Text>
                <Text size="sm">{msg.message}</Text>
                <Text size="xs" c="dimmed">
                  {msg.createdAt.substring(11, 19)}
                </Text>
              </div>
            ))}
          </div>
        </Card>
      </ScrollArea>

      {/* =================== */}
      {/* 상담 로그 (After Call) */}
      {/* =================== */}
      <Card withBorder radius="md" p="md">
        <Title order={5} mb="sm">상담 로그 (After-Call)</Title>

        {session.afterCall ? (
          <>
            <Text size="sm">
              만족도: {session.afterCall.satisfactionScore ?? "-"}
            </Text>
            <Text size="sm">
              After-call 시간: {session.afterCall.afterCallSec ?? "-"} 초
            </Text>
            <Text size="sm">
              종료 시각: {session.afterCall.endedAt ?? "-"}
            </Text>

            <Divider my="sm" />

            <Text fw={500} size="sm" mb="xs">
              상담 피드백
            </Text>
            <Text size="sm" c="dimmed">
              {session.afterCall.feedback ?? "피드백 없음"}
            </Text>
          </>
        ) : (
          <Text size="sm" c="dimmed">
            After-call 기록이 없습니다.
          </Text>
        )}
      </Card>
    </Card>
  );
}
