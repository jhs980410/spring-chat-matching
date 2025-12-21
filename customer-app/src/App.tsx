import { Routes, Route, Navigate } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "./api/axios";
import { useAuthStore } from "./stores/authStore";

import LoginPage from "./features/login/LoginPage";
import SignUpPage from "./features/login/SignUpPage";
import RequestPage from "./features/request/RequestPage";
import WaitingPage from "./features/waiting/WaitingPage";
import ChatPage from "./features/chat/ChatPage";
import SessionGate from "./features/session/SessionGate";
import HomePage from "./features/home/HomePage";

import EventDetailPage from "./features/event/pages/EventDetailPage";
import ReservePage from "./features/event/pages/ReservePage";

import TicketLayout from "./layouts/TicketLayout";
import WsGate from "./ws/WsGate";

export default function App() {
  const login = useAuthStore((s) => s.login);
  const logout = useAuthStore((s) => s.logout);
  const role = useAuthStore((s) => s.role);

  const [isInitialized, setIsInitialized] = useState(false);
  const isLoggedIn = !!role;

  useEffect(() => {
    const initAuth = async () => {
      try {
        const res = await api.get("/auth/me");
        login(res.data.id, res.data.accessToken, res.data.role);
      } catch {
        logout();
      } finally {
        setIsInitialized(true);
      }
    };

    initAuth();
  }, []);

  if (!isInitialized) return null;
return (
    <WsGate>
      <Routes>
        {/* 1. 일반 페이지 (헤더가 있는 레이아웃 적용) */}
        <Route element={<TicketLayout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/events/:id" element={<EventDetailPage />} />
        </Route>

        {/* 2. 예매 페이지 (팝업 전용: 레이아웃 외부로 분리) */}
        {/* 이렇게 외부로 꺼내야 팝업창에 헤더가 나오지 않습니다. */}
        <Route
          path="/events/:id/reserve"
          element={
            isLoggedIn ? <ReservePage /> : <Navigate to="/login" replace />
          }
        />

        {/* 3. 로그인 및 회원가입 */}
        <Route
          path="/login"
          element={isLoggedIn ? <Navigate to="/" replace /> : <LoginPage />}
        />
        <Route path="/signup" element={<SignUpPage />} />

        {/* 4. 상담 시스템 */}
        <Route
          path="/support"
          element={isLoggedIn ? <SessionGate /> : <Navigate to="/login" replace />}
        >
          <Route path="request" element={<RequestPage />} />
          <Route path="waiting" element={<WaitingPage />} />
          <Route path="chat/:sessionId" element={<ChatPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </WsGate>
  );
}
