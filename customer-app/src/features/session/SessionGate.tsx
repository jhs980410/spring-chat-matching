import { useEffect, useState } from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom"; // useLocation 추가
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
  const { pathname } = useLocation(); // ✅ 현재 브라우저의 경로를 가져옴

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

        let targetPath = "/me/support/request"; // 기본 타겟

        if (res.data) {
          const { sessionId, status } = res.data;
          if (status === "WAITING") {
            targetPath = "/me/support/waiting";
          } else if (status === "IN_PROGRESS" || status === "AFTER_CALL") {
            targetPath = `/me/support/chat/${sessionId}`;
          }
        }

        /**
         * 🔥 무한 루프 해결의 핵심!
         * 현재 경로(pathname)가 가야 할 경로(targetPath)와 다를 때만 navigate 실행.
         * 만약 이미 targetPath에 있다면 아무것도 하지 않고 checked만 true로 바꿉니다.
         */
        if (pathname !== targetPath) {
          navigate(targetPath, { replace: true });
        }
      } catch (err) {
        notifications.show({
          title: "세션 확인 실패",
          message: "다시 로그인 후 시도해주세요.",
          color: "red",
        });
        logout();
        navigate("/login", { replace: true });
      } finally {
        setChecked(true);
      }
    };

    routeBySession();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId, role]); 
  // 💡 pathname을 의존성에 넣지 마세요. 넣으면 이동할 때마다 useEffect가 다시 돌아 루프 위험이 있습니다.

  if (!checked) return null;

  return <Outlet />;
}