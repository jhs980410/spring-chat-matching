import { AppShell, Group, Text, Button, NavLink } from "@mantine/core";
import { Outlet, useNavigate, useLocation } from "react-router-dom";

export default function CounselorLayout() {
  const nav = useNavigate();
  const location = useLocation();

  const isActive = (path: string) => location.pathname.startsWith(path);

  return (
    <AppShell
      header={{ height: 60 }}
      navbar={{ width: 220, breakpoint: "sm" }}
      padding="md"
    >
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

      <AppShell.Navbar p="md" bg="#F6F7FA">
        <NavLink
          label="📊 대시보드"
          active={isActive("/dashboard")}
          onClick={() => nav("/dashboard")}
        />

        <NavLink
          label="💬 채팅 상담"
          active={isActive("/chat")}
          onClick={() => nav("/chat/1")}
        />

        <NavLink
          label="📁 상담 내역 조회"
          active={isActive("/sessions")}
          onClick={() => nav("/sessions")}
        />

        <NavLink
          label="📢 공지사항"
          active={isActive("/notices")}
          onClick={() => nav("/notices")}
        />

        <NavLink
          label="👤 내 정보 관리"
          active={isActive("/profile")}
          onClick={() => nav("/profile")}
        />
      </AppShell.Navbar>

      <AppShell.Main>
        {/* 🔥 children 대신 Outlet만 남긴다 */}
        <Outlet />
      </AppShell.Main>
    </AppShell>
  );
}
