import { useEffect, useRef } from "react";
import { Card, Text, Title, Box } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useNavigate } from "react-router-dom";

import { useAuthStore } from "../../stores/authStore";
import { useWS } from "../../api/providers/useWS";
import api from "../../api/axios";

// ===============================
// 타입 정의
// ===============================
type SessionStatus =
  | "WAITING"
  | "IN_PROGRESS"
  | "AFTER_CALL"
  | "ENDED"
  | "CANCELLED";

interface SessionResponse {
  sessionId: number;
  status: SessionStatus;
}

export default function WaitingPage() {
  const userId = useAuthStore((s) => s.userId);
  const role = useAuthStore((s) => s.role);
  const logout = useAuthStore((s) => s.logout);
  const navigate = useNavigate();

  const { connected, subscribe } = useWS();

  // 알림 중복 방지용
  const wsNotifiedRef = useRef(false);

  // ===============================
  // 0. ROLE GUARD
  // ===============================
  useEffect(() => {
    if (role === null) return;

    if (role !== "USER" || !userId) {
      notifications.show({
        title: "접근 불가",
        message: "고객 전용 페이지입니다. 다시 로그인해주세요.",
        color: "red",
      });
      logout();
      navigate("/login");
    }
  }, [role, userId, logout, navigate]);

  // ===============================
  // 1. 활성 세션 사전 확인 (REST)
  // ===============================
  useEffect(() => {
    if (role !== "USER" || !userId) return;

    const checkSession = async () => {
      try {
        const res = await api.get<SessionResponse | null>("/api/sessions/me");

        // 세션 없음 → 상담 요청 페이지로 (/me 추가)
        if (!res.data) {
          navigate("/me/support/request");
          return;
        }

        const { sessionId, status } = res.data;

        // 이미 진행 중이면 바로 채팅방으로 (/me 추가)
        if (status === "IN_PROGRESS" || status === "AFTER_CALL") {
          navigate(`/me/support/chat/${sessionId}`);
          return;
        }

        // WAITING이면 그대로 대기
        if (status === "WAITING") {
          return;
        }

        // 그 외 상태 → 다시 요청 페이지로 (/me 추가)
        navigate("/me/support/request");
      } catch (err: any) {
        if (err?.response?.status === 401) {
          notifications.show({
            title: "인증 만료",
            message: "다시 로그인해주세요.",
            color: "red",
          });
          logout();
          navigate("/login");
          return;
        }
        navigate("/me/support/request");
      }
    };

    checkSession();
  }, [role, userId, navigate, logout]);

  // ===============================
  // 2. WebSocket 구독 (WAITING → 매칭)
  // ===============================
  useEffect(() => {
    if (!connected) return;

    if (!wsNotifiedRef.current) {
      wsNotifiedRef.current = true;
      notifications.show({
        title: "연결 완료",
        message: "상담 서버에 연결되었습니다.",
      });
    }

    const unsubscribe = subscribe(
      "/sub/waiting",
      (payload: { sessionId?: number }) => {
        if (!payload?.sessionId) return;

        notifications.show({
          title: "상담 연결",
          message: "상담사와 연결되었습니다.",
        });

        // 구독 해제 후 채팅방으로 이동 (/me 추가)
        unsubscribe();
        navigate(`/me/support/chat/${payload.sessionId}`);
      }
    );

    return () => {
      unsubscribe();
    };
  }, [connected, subscribe, navigate]);

  // ===============================
  // UI
  // ===============================
  return (
    <Box p="xl">
      <Card shadow="sm" padding="xl" withBorder style={{ maxWidth: 480 }}>
        <Title order={3} fw={700}>상담 대기 중</Title>

        <Box mt="md">
          <Text size="sm" c="dimmed">로그인 ID</Text>
          <Text fw={500}>{userId}</Text>
        </Box>

        <Text mt="xl" c="dimmed" size="sm" style={{ lineHeight: 1.6 }}>
          현재 상담 연결을 위해 대기 중입니다.<br />
          상담사가 배정되면 자동으로 채팅방으로 연결됩니다. 잠시만 기다려주세요.
        </Text>

        {/* 로딩 표시용 (선택) */}
        <Box mt="lg" style={{ textAlign: 'center' }}>
           {/* 여기에 Mantine Loader 등을 추가할 수 있습니다 */}
        </Box>
      </Card>
    </Box>
  );
}