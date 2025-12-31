import { useEffect, useState } from "react";
import { Button, Card, Select, Text, Title, Box } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useNavigate } from "react-router-dom";

import api from "../../api/axios";
import { useAuthStore } from "../../stores/authStore";

type Domain = {
  id: number;
  name: string;
};

type Category = {
  id: number;
  name: string;
};

export default function RequestPage() {
  const userId = useAuthStore((s) => s.userId);
  const role = useAuthStore((s) => s.role);
  const logout = useAuthStore((s) => s.logout);
  const navigate = useNavigate();

  const [domains, setDomains] = useState<Domain[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [domainId, setDomainId] = useState<string | null>(null);
  const [categoryId, setCategoryId] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  // ===============================
  // 0. ROLE GUARD
  // ===============================
  useEffect(() => {
    if (role === null) return;

    if (role !== "USER" || !userId) {
      notifications.show({
        title: "접근 불가",
        message: "고객 전용 페이지입니다.",
        color: "red",
      });
      logout();
      navigate("/login");
    }
  }, [role, userId, logout, navigate]);

  // ===============================
  // 1. 도메인 조회
  // ===============================
  useEffect(() => {
    api.get("/domains").then((res) => {
      setDomains(res.data);
    });
  }, []);

  // ===============================
  // 2. 카테고리 조회 (domain 선택 시)
  // ===============================
  useEffect(() => {
    if (!domainId) return;

    api
      .get("/categories", {
        params: { domainId: Number(domainId) },
      })
      .then((res) => {
        setCategories(res.data);
        setCategoryId(null);
      });
  }, [domainId]);

  // ===============================
  // 3. 상담 요청 (chat_session 생성)
  // ===============================
  const requestCounsel = async () => {
    if (!domainId || !categoryId) {
      notifications.show({
        title: "선택 필요",
        message: "도메인과 카테고리를 선택해주세요.",
        color: "red",
      });
      return;
    }

    try {
      setLoading(true);

      const res = await api.post("/chat/request", {
        userId, 
        domainId: Number(domainId),
        categoryId: Number(categoryId),
      });

      const sessionId = res.data.sessionId;

      notifications.show({
        title: "상담 요청 완료",
        message: "상담 대기열에 등록되었습니다.",
      });

      // ✅ 경로 수정: /me/support/waiting 으로 이동
      navigate(`/me/support/waiting?sessionId=${sessionId}`);
    } catch (e) {
      notifications.show({
        title: "요청 실패",
        message: "상담 요청 중 오류가 발생했습니다.",
        color: "red",
      });
    } finally {
      setLoading(false);
    }
  };

  // ===============================
  // UI
  // ===============================
  return (
    <Box p="xl">
      <Card shadow="sm" padding="lg" withBorder style={{ maxWidth: 480 }}>
        <Title order={3} fw={700}>1:1 실시간 상담</Title>

        <Text size="sm" c="dimmed" mt="xs" mb="md">
          원활한 상담을 위해 상담을 원하는 항목을 선택해주세요.
        </Text>

        <Select
          label="도메인"
          placeholder="분야 선택"
          mt="md"
          value={domainId}
          onChange={setDomainId}
          data={domains.map((d) => ({
            value: String(d.id),
            label: d.name,
          }))}
        />

        <Select
          label="카테고리"
          placeholder="상세 카테고리 선택"
          mt="md"
          value={categoryId}
          onChange={setCategoryId}
          data={categories.map((c) => ({
            value: String(c.id),
            label: c.name,
          }))}
          disabled={!domainId}
        />

        <Button
          mt="xl"
          fullWidth
          size="md"
          loading={loading}
          onClick={requestCounsel}
        >
          상담 요청하기
        </Button>
      </Card>
    </Box>
  );
}