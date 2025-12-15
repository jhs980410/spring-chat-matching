import { Button, Card, TextInput, Title } from "@mantine/core";
import { useState } from "react";
import api from "../../api/axios";
import { useAuthStore } from "../../stores/authStore";
import { useNavigate } from "react-router-dom";
import { notifications } from "@mantine/notifications";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [pwd, setPwd] = useState("");

  const login = useAuthStore((s) => s.login);
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      const res = await api.post("/auth/user/login", {
        email,
        password: pwd,
      });

      /**
       * ✅ USER 로그인
       * - userId
       * - accessToken
       * - role = USER
       */
      login(res.data.id, res.data.accessToken, "USER");

      notifications.show({
        title: "로그인 성공",
        message: "세션 상태를 확인합니다.",
      });

      // ✅ 핵심 수정
      // 로그인 후 항상 루트로 이동
      navigate("/", { replace: true });
    } catch (err) {
      console.error("Login Error:", err);

      notifications.show({
        title: "로그인 실패",
        color: "red",
        message: "이메일 또는 비밀번호를 확인하세요",
      });
    }
  };

  return (
    <div style={{ width: 320, margin: "80px auto" }}>
      <Card padding="lg" shadow="sm">
        <Title order={3} mb="lg" style={{ textAlign: "center" }}>
          고객 로그인
        </Title>

        <TextInput
          label="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          mb="md"
        />

        <TextInput
          label="Password"
          value={pwd}
          type="password"
          onChange={(e) => setPwd(e.target.value)}
          mb="lg"
        />

        <Button fullWidth onClick={handleLogin}>
          로그인
        </Button>
      </Card>
    </div>
  );
}
