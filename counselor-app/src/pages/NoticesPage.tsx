import { Card, Title, Text, Stack } from "@mantine/core";
import { useNavigate } from "react-router-dom";
import CounselorLayout from "../layouts/CounselorLayout";

const mockNotices = [
  { id: 1, title: "시스템 점검 안내", date: "2025-01-12" },
  { id: 2, title: "상담 프로세스 개편 안내", date: "2025-01-10" },
];

export default function NoticesPage() {
  const nav = useNavigate();

  return (
    <CounselorLayout>
      <Title order={2} mb="md">📢 공지사항</Title>

      <Stack>
        {mockNotices.map((n) => (
          <Card
            key={n.id}
            withBorder
            shadow="sm"
            p="lg"
            style={{ cursor: "pointer" }}
            onClick={() => nav(`/notices/${n.id}`)}
          >
            <Text fw={700}>{n.title}</Text>
            <Text size="sm" c="dimmed">{n.date}</Text>
          </Card>
        ))}
      </Stack>
    </CounselorLayout>
  );
}
