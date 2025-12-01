// src/pages/SessionDetailPage.tsx
import { Card, Title, Text, Grid } from "@mantine/core";
import { useParams } from "react-router-dom";
import CounselorLayout from "../layouts/CounselorLayout";
import "./SessionDetail.css";

const dummySession = {
  id: 1,
  user: { name: "김고객", email: "user@test.com", category: "배송문의" },
  start: "2025-12-01 10:00",
  end: "2025-12-01 10:23",
  messages: [
    { sender: "USER", text: "상품 배송이 언제 되나요?", time: "10:01" },
    { sender: "COUNSELOR", text: "조회해드리겠습니다!", time: "10:02" },
    { sender: "USER", text: "넵 감사합니다!", time: "10:03" },
  ],
};

export default function SessionDetailPage() {
  const { id } = useParams();

  return (
    <CounselorLayout>
      <Title order={2} mb="lg">📁 상담 내역 상세 #{id}</Title>

      <Grid>
        {/* 고객 정보 */}
        <Grid.Col span={3}>
          <Card withBorder shadow="sm" p="md">
            <Text fw={700} mb="xs">고객 정보</Text>
            <Text size="sm">이름: {dummySession.user.name}</Text>
            <Text size="sm">이메일: {dummySession.user.email}</Text>
            <Text size="sm">카테고리: {dummySession.user.category}</Text>
          </Card>
        </Grid.Col>

        {/* 메시지 로그 */}
        <Grid.Col span={9}>
          <Card withBorder shadow="sm" p="md">
            <Text fw={700} mb="sm">메시지 기록</Text>

            <div className="timeline-box">
              {dummySession.messages.map((m, i) => (
                <div
                  key={i}
                  className={`msg-line ${m.sender === "COUNSELOR" ? "right" : "left"}`}
                >
                  <div className="bubble">
                    <Text size="sm">{m.text}</Text>
                    <Text size="xs" c="dimmed">{m.time}</Text>
                  </div>
                </div>
              ))}
            </div>

            <Text fw={700} mt="lg" mb="xs">상담 요약</Text>
            <Text size="sm">배송 조회 후 고객 안내 후 종료</Text>
          </Card>
        </Grid.Col>
      </Grid>
    </CounselorLayout>
  );
}
