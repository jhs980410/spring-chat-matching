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
        // 1. 세션 복구 시도
        const res = await api.get("/auth/me");
        
        // 2. 스토어 복구 (백엔드 응답 필드명 확인 필요: res.data.accessToken 등)
        login(res.data.id, res.data.accessToken, res.data.role); 
        console.log("상담사 세션 복구 성공");
      } catch (err) {
        console.log("상담사 세션 없음 또는 만료");
        logout(); 
      } finally {
        // 3. 확인 완료 후 렌더링 시작
        setIsInitialized(true); 
      }
    };

    initAuth();
    // 🔹 [login, logout]을 제거하여 무한 루프 원천 차단
  }, []);

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
