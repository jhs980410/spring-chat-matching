import { AppShell, Group, Text, Button, NavLink, Modal } from "@mantine/core";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import api from "../api/axios";
import { notifications } from "@mantine/notifications";
import { useDisclosure } from "@mantine/hooks";

export default function CounselorLayout() {
  const nav = useNavigate();
  const location = useLocation();

  const isActive = (path: string) => location.pathname.startsWith(path);

  // ==============================
  // 🔹 로그아웃 Confirm 모달
  // ==============================
  const [opened, { open, close }] = useDisclosure(false);

  const handleLogout = async () => {
    try {
      await api.post("/auth/logout", null, { withCredentials: true });

      notifications.show({
        color: "green",
        message: "로그아웃되었습니다.",
      });

      close();
      nav("/login", { replace: true });
    } catch (e) {
      notifications.show({
        color: "red",
        message: "로그아웃 중 오류가 발생했습니다.",
      });
    }
  };

  // ==============================
  // 🔹 진행 중 상담 조회
  // ==============================
const moveToActiveSession = async () => {
  try {
    const res = await api.get("/sessions/active", {
      withCredentials: true,
    });

    console.log("🔥 active API response:", res.data);

    if (res.data && res.data.sessionId) {
      const id = Number(res.data.sessionId);
      console.log("🔥 parsed session id:", id);

      nav(`/chat/${id}`, { replace: true });
    } else {
      notifications.show({
        color: "red",
        message: "현재 진행 중인 상담이 없습니다.",
      });
    }
  } catch (error) {
    console.error("❌ Error:", error);
    notifications.show({
      color: "red",
      message: "상담 세션 조회 중 오류가 발생했습니다.",
    });
  }
};

  return (
    <>
      <Modal
        opened={opened}
        onClose={close}
        title="로그아웃 확인"
        centered
      >
        <Text>정말 로그아웃 하시겠습니까?</Text>

        <Group mt="md" justify="flex-end">
          <Button variant="default" onClick={close}>
            취소
          </Button>
          <Button color="red" onClick={handleLogout}>
            로그아웃
          </Button>
        </Group>
      </Modal>

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
              <Button size="xs" color="red" onClick={open}>
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
    </>
  );
}
