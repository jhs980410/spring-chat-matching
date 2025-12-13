// App.tsx
import { Routes, Route, Navigate } from "react-router-dom";
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

export default function App() {
  const accessToken = useAuthStore((s) => s.accessToken);
  const isLoggedIn = !!accessToken;

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
