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

import { mockSessions } from "../../data/mock/mockSessions";
import { mockMessages } from "../../data/mock/mockMessages";
import { mockLogs } from "../../data/mock/mockLogs";
import { mockUsers } from "../../data/mock/mockUsers";
import { mockCounselors } from "../../data/mock/mockCounselors";
import { mockCategories } from "../../data/mock/mockCategories";
import { mockDomains } from "../../data/mock/mockDomains";

export default function SessionDetailPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const sid = Number(sessionId);

  const session = mockSessions.find((s) => s.id === sid);
  if (!session) {
    return (
      <Card p="lg" withBorder>
        <Title order={3}>세션이 존재하지 않습니다.</Title>
      </Card>
    );
  }

  const user = mockUsers.find((u) => u.id === session.user_id);
  const counselor = mockCounselors.find((c) => c.id === session.counselor_id);
  const category = mockCategories.find((c) => c.id === session.category_id);
  const domain = mockDomains.find((d) => d.id === session.domain_id);
  const logs = mockLogs.find((l) => l.session_id === session.id);

  const messages = mockMessages.filter((m) => m.session_id === sid);

  return (
    <Card p="lg" withBorder radius="md">
      <Title order={3} mb="md">
        상담 상세 정보 #{sid}
      </Title>

      {/* =================== */}
      {/* 고객 정보 & 상담사 정보 */}
      {/* =================== */}
      <Group align="flex-start" grow mb="xl">
        <Card withBorder radius="md" p="md">
          <Title order={5} mb="sm">
            고객 정보
          </Title>
          <Text size="sm">이름: {user?.nickname}</Text>
          <Text size="sm">이메일: {user?.email}</Text>
          <Text size="sm">상태: {user?.status}</Text>
        </Card>

        <Card withBorder radius="md" p="md">
          <Title order={5} mb="sm">
            상담사 정보
          </Title>
          <Text size="sm">이름: {counselor?.name ?? "-"}</Text>
          <Text size="sm">이메일: {counselor?.email ?? "-"}</Text>
          <Text size="sm">상태: {counselor?.status ?? "-"}</Text>
        </Card>
      </Group>

      {/* =================== */}
      {/* 세션 정보 */}
      {/* =================== */}
      <Card withBorder radius="md" p="md" mb="xl">
        <Title order={5} mb="sm">
          세션 정보
        </Title>

        <Group grow>
          <div>
            <Text size="sm">
              도메인: <b>{domain?.name}</b>
            </Text>
            <Text size="sm">
              카테고리: <b>{category?.name}</b>
            </Text>
            <Text size="sm">
              상태:{" "}
              <Badge
                color={
                  session.status === "IN_PROGRESS"
                    ? "blue"
                    : session.status === "ENDED"
                    ? "green"
                    : "gray"
                }
              >
                {session.status}
              </Badge>
            </Text>
          </div>

          <div>
            <Text size="sm">요청 시각: {session.requested_at}</Text>
            <Text size="sm">배정 시각: {session.assigned_at ?? "-"}</Text>
            <Text size="sm">시작 시각: {session.started_at ?? "-"}</Text>
            <Text size="sm">종료 시각: {session.ended_at ?? "-"}</Text>
            <Text size="sm">
              상담 시간: {session.duration_sec ? `${session.duration_sec}s` : "-"}
            </Text>
          </div>
        </Group>
      </Card>

      {/* =================== */}
      {/* 메시지 타임라인 */}
      {/* =================== */}
      <Title order={5} mb="sm">
        메시지 기록
      </Title>
      <ScrollArea h={300} scrollbarSize={6} mb="xl">
        <Card withBorder radius="md" p="md">
          <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
            {messages.map((msg) => (
              <div
                key={msg.id}
                style={{
                  alignSelf:
                    msg.sender_type === "COUNSELOR" ? "flex-end" : "flex-start",
                  backgroundColor:
                    msg.sender_type === "COUNSELOR" ? "#d0ebff" : "#f1f3f5",
                  padding: "8px 12px",
                  borderRadius: 10,
                  maxWidth: "75%",
                }}
              >
                <Text size="sm" fw={500}>
                  {msg.sender_type === "COUNSELOR"
                    ? counselor?.name || "상담사"
                    : user?.nickname}
                </Text>
                <Text size="sm">{msg.message}</Text>
                <Text size="xs" c="dimmed">
                  {msg.created_at.substring(11, 19)}
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
        <Title order={5} mb="sm">
          상담 로그 (After-Call)
        </Title>

        {logs ? (
          <>
            <Text size="sm">만족도: {logs.satisfaction_score ?? "-"}</Text>
            <Text size="sm">After-call 시간: {logs.after_call_sec} 초</Text>

            <Text size="sm">종료 시각: {logs.ended_at ?? "-"}</Text>

            <Divider my="sm" />

            <Text fw={500} size="sm" mb="xs">
              상담 피드백
            </Text>
            <Text size="sm" c="dimmed">
              {logs.feedback ?? "피드백 없음"}
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
