import { Button, Card, TextInput, Title } from "@mantine/core";
import { useState } from "react";
import axios from "axios";
import { useAuthStore } from "../../stores/authStore";
import { useNavigate } from "react-router-dom";
import { notifications } from "@mantine/notifications";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [pwd, setPwd] = useState("");

  const login = useAuthStore((s) => s.login);
  const navigate = useNavigate();

  // handleLogin 함수는 컴포넌트 함수 LoginPage 내부에 완전히 정의되어야 합니다.
  const handleLogin = async () => {
    try {
      const res = await axios.post("http://localhost:8080/api/auth/counselor/login", {
        email,
        password: pwd,
      });

      login(res.data.userId, res.data.accessToken);

      notifications.show({
        title: "로그인 성공",
        message: "상담사 페이지로 이동합니다",
      });

      navigate("/dashboard");
    } catch (err) {
      console.error("Login Error:", err);

      notifications.show({
        title: "로그인 실패",
        color: "red",
        message: "이메일 또는 비밀번호를 확인하세요",
      });
    }
  }; // <--- handleLogin 함수는 여기서 닫힙니다. (JSX return 이전에)

  // LoginPage 컴포넌트 함수는 여기서 JSX를 반환합니다.
  return (
    <div style={{ width: 320, margin: "80px auto" }}>
      <Card padding="lg" shadow="sm">
        <Title order={3} mb="lg" style={{ textAlign: "center" }}>
          상담사 로그인
        </Title>

        <TextInput label="Email" value={email} onChange={(e) => setEmail(e.target.value)} mb="md" />
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
// 불필요한 마지막 닫는 중괄호를 제거했습니다.