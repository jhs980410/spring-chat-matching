import { AppShell, Group, Text, Button, NavLink } from "@mantine/core";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import { notifications } from "@mantine/notifications";

export default function CounselorLayout() {
  const nav = useNavigate();
  const location = useLocation();

  const isActive = (path: string) => location.pathname.startsWith(path);

  // ================================
  // 진행 중 상담 조회 후 이동
  // ================================
  const moveToActiveSession = async () => {
    try {
      const res = await axios.get("/api/sessions/active", {
        withCredentials: true,
      });

      if (res.data && res.data.sessionId) {
        nav(`/chat/${res.data.sessionId}`);
      } else {
        notifications.show({
          color: "red",
          message: "현재 진행 중인 상담이 없습니다.",
        });
      }
    } catch (error) {
      console.error(error);
      notifications.show({
        color: "red",
        message: "상담 세션 조회 중 오류가 발생했습니다.",
      });
    }
  };

  return (
    <AppShell
      header={{ height: 60 }}
      navbar={{ width: 220, breakpoint: "sm" }}
      padding="md"
    >
      {/* ================= Header ================= */}
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

      {/* ================= Sidebar ================= */}
      <AppShell.Navbar p="md" bg="#F6F7FA">
        <NavLink
          label="📊 대시보드"
          active={isActive("/dashboard")}
          onClick={() => nav("/dashboard")}
        />

        <NavLink
          label="💬 진행 중 상담"
          active={isActive("/chat")}
          onClick={moveToActiveSession}
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

      {/* ================= Main ================= */}
      <AppShell.Main>
        <Outlet />
      </AppShell.Main>
    </AppShell>
  );
}
