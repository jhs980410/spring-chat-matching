import { AppShell, Group, Text, Button, Card, Grid } from "@mantine/core";
import { useNavigate, useLocation } from "react-router-dom";

export default function CounselorLayout({ children }: { children: React.ReactNode }) {
  const nav = useNavigate();
  const location = useLocation();

  const isActive = (path: string) => location.pathname.startsWith(path);

  return (
    <AppShell
      header={{ height: 60 }}
      navbar={{ width: 200, breakpoint: "sm" }}
      padding="md"
    >
      {/* 상단바 */}
      <AppShell.Header>
        <Group
          justify="space-between"
          px="lg"
          style={{
            height: "100%",
            backgroundColor: "#1A4DBE",
          }}
        >
          <Text fw={700} size="lg" c="white">
            통합 상담센터
          </Text>

          <Group>
            <Text c="white">상담사: 홍길동</Text>
            <Button size="xs" color="red" onClick={() => nav("/login")}>
              로그아웃
            </Button>
          </Group>
        </Group>
      </AppShell.Header>

      {/* 좌측 메뉴 */}
      <AppShell.Navbar p="md" bg="#F6F7FA">

        <Button
          variant={isActive("/dashboard") ? "filled" : "subtle"}
          fullWidth
          mb="xs"
          styles={{
            root: {
              backgroundColor: isActive("/dashboard") ? "#e7f1ff" : "transparent",
              fontWeight: isActive("/dashboard") ? 700 : 400,
              color: "#1A4DBE",
            },
          }}
          onClick={() => nav("/dashboard")}
        >
          📊 대시보드
        </Button>

        <Button
          variant={isActive("/chat") ? "filled" : "subtle"}
          fullWidth
          mb="xs"
          styles={{
            root: {
              backgroundColor: isActive("/chat") ? "#e7f1ff" : "transparent",
              fontWeight: isActive("/chat") ? 700 : 400,
              color: "#1A4DBE",
            },
          }}
          onClick={() => nav("/chat/1")}
        >
          💬 채팅 상담
        </Button>

        <Button
          variant={isActive("/sessions/history") ? "filled" : "subtle"}
          fullWidth
          mb="xs"
          styles={{
            root: {
              backgroundColor: isActive("/sessions/history") ? "#e7f1ff" : "transparent",
              fontWeight: isActive("/sessions/history") ? 700 : 400,
              color: "#1A4DBE",
            },
          }}
          onClick={() => nav("/sessions/history")}
        >
          📁 상담 내역 조회
        </Button>

        <Button
          variant={isActive("/notices") ? "filled" : "subtle"}
          fullWidth
          mb="xs"
          styles={{
            root: {
              backgroundColor: isActive("/notices") ? "#e7f1ff" : "transparent",
              fontWeight: isActive("/notices") ? 700 : 400,
              color: "#1A4DBE",
            },
          }}
          onClick={() => nav("/notices")}
        >
          📢 공지사항
        </Button>

        <Button
          variant={isActive("/profile") ? "filled" : "subtle"}
          fullWidth
          mb="xs"
          styles={{
            root: {
              backgroundColor: isActive("/profile") ? "#e7f1ff" : "transparent",
              fontWeight: isActive("/profile") ? 700 : 400,
              color: "#1A4DBE",
            },
          }}
          onClick={() => nav("/profile")}
        >
          👤 내 정보 관리
        </Button>

      </AppShell.Navbar>

      {/* 메인 영역 */}
      <AppShell.Main>

        {/* 🔵 상단 글로벌 대시보드 */}
        <Grid mb="lg">
          <Grid.Col span={4}>
            <Card withBorder shadow="sm" p="md">
              <Text fw={700}>오늘 상담 수</Text>
              <Text size="xl">12</Text>
            </Card>
          </Grid.Col>

          <Grid.Col span={4}>
            <Card withBorder shadow="sm" p="md">
              <Text fw={700}>대기 중</Text>
              <Text size="xl">3</Text>
            </Card>
          </Grid.Col>

          <Grid.Col span={4}>
            <Card withBorder shadow="sm" p="md">
              <Text fw={700}>평균 상담 시간</Text>
              <Text size="xl">147초</Text>
            </Card>
          </Grid.Col>
        </Grid>

        {/* 페이지 내용 */}
        {children}
      </AppShell.Main>
    </AppShell>
  );
}
