import { useParams, Link } from "react-router-dom";
import { Card, Title, Text, Divider, Button, Image } from "@mantine/core";

// 하드코딩된 공지 상세 데이터
const noticeDetails = {
  1: {
    title: "📢 시스템 점검 안내",
    author: "관리자",
    created_at: "2024-12-10 10:00",
    content: `
12월 12일 새벽 2시부터 4시까지 내부 시스템 점검이 진행됩니다.

해당 시간 동안 상담 서비스 이용이 일시적으로 중단될 수 있습니다.
서비스 이용에 불편을 드려 죄송합니다.

보다 안정적인 서비스 제공을 위해 필요한 작업이니 양해 부탁드립니다.`,
    image: null,
  },
  2: {
    title: "🎉 상담 서비스 신규 기능 업데이트",
    author: "운영팀",
    created_at: "2024-12-09 14:30",
    content: `
새로운 상담사 대시보드 기능이 추가되었습니다.

- 일일 상담 통계
- 평균 상담 시간 분석
- 상담 카테고리별 KPI
- 실시간 상담 현황 모니터링

앞으로도 더욱 편리한 상담 기능을 위해 업데이트가 계속될 예정입니다!`,
    image: "https://images.unsplash.com/photo-1563986768494-4dee2763ff3f",
  },
  3: {
    title: "📄 개인정보 처리 방침 변경",
    author: "보안팀",
    created_at: "2024-12-05 09:00",
    content: `
개인정보 처리 방침이 아래와 같이 변경되었습니다.

- 수집 항목 일부 변경
- 파기 절차 명시
- 개인정보 위탁업체 정보 추가

자세한 내용은 홈페이지 개인정보 처리방침 페이지를 참고해주세요.`,
    image: null,
  },
};

export default function NoticesDetailPage() {
  const { noticeId } = useParams<{ noticeId: string }>();
  const id = Number(noticeId);

  const data = noticeDetails[id];
  if (!data) {
    return (
      <Card p="lg" withBorder>
        <Title order={3}>공지사항을 찾을 수 없습니다.</Title>
      </Card>
    );
  }

  return (
    <Card p="lg" withBorder radius="md">
      <Title order={2} mb="xs">
        {data.title}
      </Title>

      <Text size="sm" c="dimmed">
        작성자: {data.author} · {data.created_at}
      </Text>

      <Divider my="sm" />

      {data.image && (
        <Image
          src={data.image}
          alt="notice-image"
          radius="md"
          mb="md"
          fit="cover"
        />
      )}

      <Text size="sm" style={{ whiteSpace: "pre-line" }}>
        {data.content}
      </Text>

      <Divider my="lg" />

      <Button component={Link} to="/notices" variant="light">
        목록으로 돌아가기
      </Button>
    </Card>
  );
}
