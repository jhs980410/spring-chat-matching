import { Button, Card, TextInput, Title } from "@mantine/core";
import { useState } from "react";
import api from "../../api/axios";
import { useAuthStore } from "../../stores/authStore";
import { useNavigate , useSearchParams} from "react-router-dom";
import { notifications } from "@mantine/notifications";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [pwd, setPwd] = useState("");

  const login = useAuthStore((s) => s.login);
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const redirect = params.get("redirect") || "/";
const handleLogin = async () => {
  try {
    const res = await api.post("/auth/user/login", {
      email,
      password: pwd,
    });

    login(res.data.id, res.data.accessToken, "USER");

    notifications.show({
      title: "로그인 성공",
      message: "이전 화면으로 이동합니다.",
    });

    // 🔥 핵심: redirect 적용
    navigate(redirect, { replace: true });

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
         계정 로그인
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
      <div style={{ display: 'flex', justifyContent: 'center', marginTop: '10px' }}>
  <span style={{ fontSize: '14px', color: '#868e96' }}>계정이 없으신가요?</span>
  <Button variant="transparent" size="xs" onClick={() => navigate("/signup")} style={{ padding: '0 5px' }}>
    회원가입
  </Button>
</div>
         
      </Card>
    </div>
  );
}
