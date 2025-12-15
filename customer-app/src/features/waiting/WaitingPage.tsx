// features/waiting/WaitingPage.tsx

import { useEffect, useRef } from "react";
import { Card, Text, Title } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useNavigate } from "react-router-dom";

import { useAuthStore } from "../../stores/authStore";
import { useWS } from "../../api/providers/useWS";
import api from "../../api/axios";

// ===============================
// 타입 정의 (백엔드 기준)
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

        // 세션 없음 → 상담 요청 페이지
        if (!res.data) {
          navigate("/request");
          return;
        }

        const { sessionId, status } = res.data;

        // 이미 진행 중이면 바로 채팅방
        if (status === "IN_PROGRESS" || status === "AFTER_CALL") {
          navigate(`/chat/${sessionId}`);
          return;
        }

        // WAITING이면 그대로 대기
        if (status === "WAITING") {
          return;
        }

        // 종료된 세션이면 다시 요청
        navigate("/request");
      } catch (err: any) {
        // 인증 만료 or 세션 조회 불가
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

        // 기타 오류 → 요청 페이지로
        navigate("/request");
      }
    };

    checkSession();
  }, [role, userId, navigate, logout]);

  // ===============================
  // 2. WebSocket 구독 (WAITING → 매칭)
  // ===============================
  useEffect(() => {
    if (!connected) return;

    // 연결 알림은 1회만
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

          navigate(`/chat/${payload.sessionId}`);
        }
      );

    return () => {
      unsubscribe?.();
    };
  }, [connected, subscribe, navigate]);

  // ===============================
  // UI
  // ===============================
  return (
    <div style={{ maxWidth: 480, margin: "60px auto" }}>
      <Card shadow="sm" padding="lg">
        <Title order={3}>상담 대기 중</Title>

        <Text mt="md">
          로그인 ID: <b>{userId}</b>
        </Text>

        <Text size="sm" c="dimmed" mt="xs">
          role: {role}
        </Text>

        <Text mt="sm" c="dimmed">
          상담사와 연결될 때까지 잠시 기다려주세요.
        </Text>
      </Card>
    </div>
  );
}
