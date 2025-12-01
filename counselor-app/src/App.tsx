import { BrowserRouter, Routes, Route } from "react-router-dom";

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
  return (
   
      <Routes>
        {/* 로그인 */}
        <Route path="/login" element={<LoginPage />} />

        {/* 상담사 레이아웃 지정 */}
        <Route element={<CounselorLayout />}>
          {/* 상담 */}
          <Route path="/chat/:sessionId" element={<ChatPage />} />

          {/* 상담 내역 */}
          <Route path="/sessions" element={<SessionHistoryPage />} />
          <Route path="/sessions/:sessionId" element={<SessionDetailPage />} />

          {/* 공지사항 */}
          <Route path="/notices" element={<NoticesPage />} />
          <Route path="/notices/:noticeId" element={<NoticesDetailPage />} />

          {/* 마이페이지 */}
          <Route path="/profile" element={<ProfilePage />} />

          {/* 대시보드 */}
          <Route path="/dashboard" element={<DashboardPage />} />
        </Route>

        {/* 기본 리다이렉트 */}
        <Route path="*" element={<LoginPage />} />
      </Routes>
   
  );
}
