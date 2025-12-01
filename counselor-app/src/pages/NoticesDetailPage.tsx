// src/pages/NoticesDetailPage.tsx
import { Card, Title, Text, Button, Image } from "@mantine/core";
import { useNavigate, useParams } from "react-router-dom";
import CounselorLayout from "../layouts/CounselorLayout";

export default function NoticesDetailPage() {
  const nav = useNavigate();
  const { id } = useParams();

  const dummyNotice = {
    title: "시스템 점검 안내",
    date: "2025-12-02",
    content: "12월 2일 새벽 2시~5시 시스템 점검 예정입니다.",
    image: "https://placehold.co/800x300/1A4DBE/FFFFFF?text=Notice+Banner",
    fileUrl: "/notice-file.pdf",
  };

  return (
    <CounselorLayout>
      <Title order={2} mb="lg">📢 공지사항 #{id}</Title>

      <Card withBorder shadow="sm" p="lg">

        <Text fw={700} size="xl" mb="xs">{dummyNotice.title}</Text>
        <Text c="dimmed" size="sm" mb="lg">{dummyNotice.date}</Text>

        {/* 이미지 */}
        <Image src={dummyNotice.image} radius="md" mb="lg" />

        {/* 내용 */}
        <Text mb="xl">{dummyNotice.content}</Text>

        {/* 파일 */}
        <Button
          variant="light"
          component="a"
          href={dummyNotice.fileUrl}
          download
          mb="lg"
        >
          📎 첨부파일 다운로드
        </Button>

        <Button variant="outline" onClick={() => nav("/notices")}>
          목록으로 돌아가기
        </Button>
      </Card>
    </CounselorLayout>
  );
}
