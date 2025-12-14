import { useEffect } from "react";
import { Button, Card, Text, Title } from "@mantine/core";
import { notifications } from "@mantine/notifications";

import api from "../../api/axios";
import { wsClient } from "../../ws/wsClient";
import { useAuthStore } from "../../stores/authStore";

export default function WaitingPage() {
  const counselorId = useAuthStore((s) => s.counselorId);
  const role = useAuthStore((s) => s.role);

  // ===============================
  // 1. WebSocket CONNECT
  // ===============================
  useEffect(() => {
    console.log("[Waiting] WS connect start");

    wsClient.connect(
      () => {
        console.log("[Waiting] WS connected");

        notifications.show({
          title: "연결 완료",
          message: "상담 서버에 연결되었습니다",
        });

        // 👉 대기 상태용 (지금은 로그만)
        wsClient.subscribe("/sub/waiting", (msg) => {
          console.log("[Waiting][WS]", msg);
        });
      },
      (err) => {
        console.error("[Waiting] WS error", err);
      }
    );

    return () => {
      wsClient.disconnect();
      console.log("[Waiting] WS disconnected");
    };
  }, []);

  // ===============================
  // 2. 상담 요청 (Redis enqueue)
  // ===============================
  const requestMatch = async () => {
    try {
      await api.post("/match/request", {
        domainId: 1,     // 임시
        categoryId: 1,   // 임시
      });

      notifications.show({
        title: "상담 요청 완료",
        message: "상담 대기열에 등록되었습니다",
      });
    } catch (e) {
      notifications.show({
        title: "요청 실패",
        color: "red",
        message: "상담 요청 중 오류가 발생했습니다",
      });
    }
  };

  return (
    <div style={{ maxWidth: 480, margin: "60px auto" }}>
      <Card shadow="sm" padding="lg">
        <Title order={3}>상담 대기 중</Title>

        <Text mt="md">
          로그인 ID: <b>{counselorId}</b>
        </Text>

        <Text size="sm" c="dimmed" mt="xs">
          role: {role}
        </Text>

        <Text mt="sm" c="dimmed">
          상담사와 연결될 때까지 잠시 기다려주세요.
        </Text>

        <Button mt="lg" fullWidth onClick={requestMatch}>
          상담 요청
        </Button>
      </Card>
    </div>
  );
}
