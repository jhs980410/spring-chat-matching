// src/pages/ChatPage.tsx
import { useParams } from "react-router-dom";
import {
  Title,
  Card,
  Grid,
  Text,
  Textarea,
  Button,
  Select,
  Checkbox,
} from "@mantine/core";
import "./ChatPage.css";
import CounselorLayout from "../layouts/CounselorLayout";

const mockMessages = [
  { sender: "USER", text: "안녕하세요?", time: "10:01" },
  { sender: "COUNSELOR", text: "무엇을 도와드릴까요?", time: "10:01" },
];

export default function ChatPage() {
  const { sessionId } = useParams();

  return (
    <CounselorLayout>
      <Title order={2} mb="md">
        채팅 세션 #{sessionId}
      </Title>

      <Grid gutter="xl">
        {/* ▣ 좌측 고객 정보 */}
        <Grid.Col span={3}>
          <Card withBorder shadow="sm" p="md" radius="md">
            <Text fw={700} mb="xs">
              고객 정보
            </Text>

            <Text size="sm">이름: 김고객</Text>
            <Text size="sm">이메일: user@test.com</Text>
            <Text size="sm">카테고리: 배송문의</Text>
          </Card>
        </Grid.Col>

        {/* ▣ 중앙 채팅 UI */}
        <Grid.Col span={6}>
          <Card withBorder shadow="sm" p="md" radius="md">
            {/* 메시지 영역 */}
            <div className="chat-box">
              {mockMessages.map((m, i) => (
                <div
                  key={i}
                  className={`msg-row ${
                    m.sender === "COUNSELOR" ? "me" : "other"
                  }`}
                >
                  <div
                    className={`msg-bubble ${
                      m.sender === "COUNSELOR" ? "me-bubble" : "other-bubble"
                    }`}
                  >
                    {m.text}
                  </div>

                  <Text size="xs" c="dimmed" mt={4}>
                    {m.time}
                  </Text>
                </div>
              ))}
            </div>

            {/* 입력창 */}
            <Textarea placeholder="메시지를 입력하세요" mt="md" radius="md" />
            <Button fullWidth mt="sm" radius="md">
              전송
            </Button>
          </Card>
        </Grid.Col>

        {/* ▣ 우측 상담 상태 + 상담결과 입력 */}
        <Grid.Col span={3}>
          <Card withBorder shadow="sm" p="md" radius="md" style={{ height: "100%", overflowY: "auto" }}>
            <Text fw={700} mb="sm">
              상담 상태
            </Text>

            <Text size="sm">시작: 10:00</Text>
            <Text size="sm" mb="md">
              경과: 32초
            </Text>

            {/* --- 상담 입력 필드 --- */}

            <Select
              label="상담 대분류"
              placeholder="선택"
              data={["배송", "환불", "회원", "기타"]}
              mb="sm"
            />

            <Select
              label="상담 중분류"
              placeholder="선택"
              data={["지연", "파손", "오배송"]}
              mb="sm"
            />

            <Select
              label="상담 소분류"
              placeholder="선택"
              data={["일반택배", "퀵서비스", "국제배송"]}
              mb="sm"
            />

            <Select
              label="상담 결과"
              placeholder="상담 결과 선택"
              data={["완료", "추가문의 필요", "콜백 필요"]}
              mb="sm"
            />

            <Checkbox label="콜백 필요" mb="md" />

            <Textarea
              label="상담 메모"
              placeholder="상담 내용을 입력하세요…"
              minRows={4}
              radius="md"
              mb="md"
            />

            <Button fullWidth radius="md" color="green" mb="sm">
              상담 저장
            </Button>

            <Button fullWidth radius="md" color="red">
              상담 종료
            </Button>
          </Card>
        </Grid.Col>
      </Grid>
    </CounselorLayout>
  );
}
