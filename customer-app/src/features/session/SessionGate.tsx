// features/session/SessionGate.tsx
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { notifications } from "@mantine/notifications";

import { useAuthStore } from "../../stores/authStore";
import api from "../../api/axios";

type SessionStatus =
  | "WAITING"
  | "IN_PROGRESS"
  | "AFTER_CALL"
  | "ENDED"
  | "CANCELLED";

export default function SessionGate() {
  const userId = useAuthStore((s) => s.userId);
  const role = useAuthStore((s) => s.role);
  const logout = useAuthStore((s) => s.logout);
  const navigate = useNavigate();

  useEffect(() => {
    if (!userId || role !== "USER") return;

    const routeBySession = async () => {
      try {
        const res = await api.get<{
          sessionId: number;
          status: SessionStatus;
        } | null>("/sessions/me");

        if (!res.data) {
          navigate("/request", { replace: true });
          return;
        }

        const { sessionId, status } = res.data;

        if (status === "WAITING") {
          navigate("/waiting", { replace: true });
          return;
        }

        if (status === "IN_PROGRESS" || status === "AFTER_CALL") {
          navigate(`/chat/${sessionId}`, { replace: true });
          return;
        }

        navigate("/request", { replace: true });
      } catch {
        notifications.show({
          title: "세션 확인 실패",
          message: "다시 로그인해주세요.",
          color: "red",
        });
        logout();
        navigate("/login", { replace: true });
      }
    };

    routeBySession();
  }, [userId, role, navigate, logout]);

  return null; // 라우팅만 수행
}
