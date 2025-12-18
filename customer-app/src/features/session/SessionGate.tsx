// features/session/SessionGate.tsx
import { useEffect, useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
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

  const [checked, setChecked] = useState(false);

  useEffect(() => {
    if (!userId || role !== "USER") {
      setChecked(true);
      return;
    }

    const routeBySession = async () => {
      try {
        const res = await api.get<{
          sessionId: number;
          status: SessionStatus;
        } | null>("/sessions/me");

        if (!res.data) {
          navigate("/support/request", { replace: true });
          return;
        }

        const { sessionId, status } = res.data;

        if (status === "WAITING") {
          navigate("/support/waiting", { replace: true });
          return;
        }

        if (status === "IN_PROGRESS" || status === "AFTER_CALL") {
          navigate(`/support/chat/${sessionId}`, { replace: true });
          return;
        }

        navigate("/support/request", { replace: true });
      } catch {
        notifications.show({
          title: "세션 확인 실패",
          message: "다시 로그인해주세요.",
          color: "red",
        });
        logout();
        navigate("/login", { replace: true });
      } finally {
        setChecked(true);
      }
    };

    routeBySession();
  }, [userId, role, navigate, logout]);

  if (!checked) return null; // 로딩 스핀 넣어도 됨

  return <Outlet />;
}
