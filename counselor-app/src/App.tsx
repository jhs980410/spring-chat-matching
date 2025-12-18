// App.tsx
import { Routes, Route, Navigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useAuthStore } from "./stores/authStore";

import CounselorLayout from "./layouts/CounselorLayout";

import ChatPage from "./features/chat/pages/ChatPage";
import SessionHistoryPage from "./features/sessions/SessionHistoryPage";
import SessionDetailPage from "./features/sessions/SessionDetailPage";
import NoticesPage from "./features/notices/NoticesPage";
import NoticesDetailPage from "./features/notices/NoticesDetailPage";
import DashboardPage from "./features/dashboard/DashboardPage";
import ProfilePage from "./features/profile/ProfilePage";
import LoginPage from "./features/login/LoginPage";
import api from "./api/axios";
export default function App() {
  const accessToken = useAuthStore((s) => s.accessToken);
  const isLoggedIn = !!accessToken;
  const login = useAuthStore((s) => s.login);
  const logout = useAuthStore((s) => s.logout);
  const [isInitialized, setIsInitialized] = useState(false); // 🔥 초기화 로딩 상태

  useEffect(() => {
    const initAuth = async () => {
      try {
        // 1. 서버의 /me 엔드포인트를 호출하여 쿠키 내 토큰 확인
        const res = await api.get("/auth/me");
        
        // 2. 성공 시 Zustand 스토어 복구
        // res.data는 { id, role, ... } 형태라고 가정
        login(res.data.id, res.data.role); 
      } catch (err) {
        console.log("세션이 없거나 만료되었습니다.");
        logout(); // 실패 시 스토어 초기화
      } finally {
        setIsInitialized(true); // 3. 확인이 끝나면 화면을 보여줌
      }
    };

    initAuth();
  }, [login, logout]);

  // 인증 확인이 끝나기 전에는 아무것도 렌더링하지 않거나 로딩 스피너를 보여줌
  // 이렇게 해야 '깜빡임' 현상(잠깐 로그인 페이지 보였다가 메인 가는 현상)이 방지됩니다.
  if (!isInitialized) return null;
  return (
    <Routes>
      {/* 로그인 */}
      <Route path="/login" element={<LoginPage />} />

      {/* 보호된 영역 */}
      <Route
        element={
          isLoggedIn ? <CounselorLayout /> : <Navigate to="/login" replace />
        }
      >
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/chat/:sessionId" element={<ChatPage />} />
        <Route path="/sessions" element={<SessionHistoryPage />} />
        <Route path="/sessions/:sessionId" element={<SessionDetailPage />} />
        <Route path="/notices" element={<NoticesPage />} />
        <Route path="/notices/:noticeId" element={<NoticesDetailPage />} />
        <Route path="/profile" element={<ProfilePage />} />
      </Route>

      {/* 그 외 */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
