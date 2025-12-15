import { Routes, Route, Navigate } from "react-router-dom";

import LoginPage from "./features/login/LoginPage";
import RequestPage from "./features/request/RequestPage";
import WaitingPage from "./features/waiting/WaitingPage";
import ChatPage from "./features/chat/ChatPage";
import SessionGate from "./features/session/SessionGate";

export default function App() {
  return (
    <Routes>
      {/* 🔥 루트 = 세션 판단 전용 */}
      <Route path="/" element={<SessionGate />} />

      {/* 로그인 */}
      <Route path="/login" element={<LoginPage />} />

      {/* 상담 요청 */}
      <Route path="/request" element={<RequestPage />} />

      {/* 상담 대기 */}
      <Route path="/waiting" element={<WaitingPage />} />

      {/* 채팅 */}
      <Route path="/chat/:sessionId" element={<ChatPage />} />

      {/* 없는 경로 */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
