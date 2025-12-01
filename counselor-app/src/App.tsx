import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import ChatPage from "./pages/ChatPage";
import SessionHistoryPage from "./pages/SessionHistoryPage";
import SessionDetailPage from "./pages/SessionDetailPage";
import NoticesPage from "./pages/NoticesPage";
import NoticesDetailPage from "./pages/NoticesDetailPage";
import ProfilePage from "./pages/ProfilePage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />

      <Route path="/login" element={<LoginPage />} />

      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/chat/:sessionId" element={<ChatPage />} />

      <Route path="/sessions/history" element={<SessionHistoryPage />} />
      <Route path="/sessions/:id" element={<SessionDetailPage />} />

      <Route path="/notices" element={<NoticesPage />} />
      <Route path="/notices/:id" element={<NoticesDetailPage />} />

      <Route path="/profile" element={<ProfilePage />} />

      <Route path="*" element={<h1>404 Not Found</h1>} />
    </Routes>
  );
}
