import { Group, Button, Text } from "@mantine/core";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuthStore } from "../stores/authStore";
import api from "../api/axios";
import { notifications } from "@mantine/notifications";

export default function Header() {
  const navigate = useNavigate();
  const location = useLocation();

  const userId = useAuthStore((s) => s.userId);
  const logout = useAuthStore((s) => s.logout);

  const handleLogin = () => {
    navigate(`/login?redirect=${location.pathname}`);
  };

  const handleLogout = async () => {
    try {
      // 🔥 서버 로그아웃 (쿠키 삭제)
      await api.post("/auth/logout");

      // 🔥 클라이언트 상태 초기화
      logout();

      notifications.show({
        title: "로그아웃",
        message: "정상적으로 로그아웃되었습니다.",
      });

      navigate("/");
    } catch (e) {
      notifications.show({
        title: "로그아웃 실패",
        message: "다시 시도해주세요.",
        color: "red",
      });
    }
  };

  return (
    <Group
  justify="space-between"
  px="lg"
  py="md"
  style={{
    borderBottom: "1px solid #eee",
    position: "sticky",
    top: 0,
    background: "white",
    zIndex: 10,
  }}
>
      {/* 좌측 로고 */}
      <Text
        fw={700}
        size="lg"
        style={{ cursor: "pointer" }}
        onClick={() => navigate("/")}
      >
        TICKET
      </Text>

      {/* 우측 계정 영역 */}
      {!userId ? (
        <Button onClick={handleLogin}>로그인</Button>
      ) : (
        <Group>
<Button
  variant="subtle"
  onClick={() => navigate("/me")}
>
  마이페이지
</Button>
          <Button variant="light" color="red" onClick={handleLogout}>
            로그아웃
          </Button>
        </Group>
      )}
    </Group>
  );
}
