import { Button, Card, TextInput, Title, Text, Group, Anchor, Stack } from "@mantine/core";
import { useState } from "react";
import api from "../../api/axios"; // 기존 설정된 axios 인스턴스 활용
import { useNavigate } from "react-router-dom";
import { notifications } from "@mantine/notifications";

export default function SignupPage() {
  const [email, setEmail] = useState("");
  const [pwd, setPwd] = useState("");
  const [nickname, setNickname] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleSignup = async () => {
    // 간단한 유효성 검사
    if (!email || !pwd || !nickname) {
      notifications.show({
        title: "입력 오류",
        message: "모든 필드를 입력해주세요.",
        color: "red",
      });
      return;
    }

    setLoading(true);
    try {
      // 백엔드 UserSignupRequest 구조에 맞게 데이터 전송
      await api.post("/auth/user/signup", {
        email: email,
        password: pwd,
        nickname: nickname,
      });

      notifications.show({
        title: "회원가입 성공",
        message: "로그인 화면으로 이동합니다. 가입한 계정으로 로그인해주세요!",
        color: "green",
      });

      // 가입 성공 후 로그인 페이지로 이동
      navigate("/login");
    } catch (err: any) {
      console.error("Signup Error:", err);
      notifications.show({
        title: "회원가입 실패",
        message: err.response?.data?.message || "이미 존재하는 이메일이거나 서버 오류가 발생했습니다.",
        color: "red",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ width: 320, margin: "80px auto" }}>
      <Card padding="lg" shadow="sm" withBorder>
        <Title order={3} mb="lg" style={{ textAlign: "center" }}>
          회원가입
        </Title>

        <Stack gap="md">
          <TextInput
            label="Email"
            placeholder="example@email.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <TextInput
            label="Nickname"
            placeholder="사용할 닉네임을 입력하세요"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            required
          />

          <TextInput
            label="Password"
            placeholder="비밀번호를 입력하세요"
            value={pwd}
            type="password"
            onChange={(e) => setPwd(e.target.value)}
            required
          />

          <Button 
            fullWidth 
            onClick={handleSignup} 
            loading={loading}
            mt="md"
          >
            가입하기
          </Button>
        </Stack>

        <Group justify="center" mt="xl" gap={5}>
          <Text size="sm" c="dimmed">
            이미 계정이 있으신가요?
          </Text>
          <Anchor
            component="button"
            type="button"
            size="sm"
            fw={500}
            onClick={() => navigate("/login")}
          >
            로그인
          </Anchor>
        </Group>
      </Card>
    </div>
  );
}