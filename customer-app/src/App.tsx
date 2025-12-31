import { Routes, Route, Navigate } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "./api/axios";
import { useAuthStore } from "./stores/authStore";

import WsGate from "./ws/WsGate";

/* auth */
import LoginPage from "./features/login/LoginPage";
import SignUpPage from "./features/login/SignUpPage";

/* home */
import HomePage from "./features/home/HomePage";

/* ticket */
import TicketLayout from "./layouts/TicketLayout";
import EventDetailPage from "./features/event/pages/EventDetailPage";
import ReservePage from "./features/event/pages/ReservePage";

/* payment */
import PaymentConfirmPage from "./features/payment/PaymentConfirmPage";
import PaymentSuccessPage from "./features/payment/PaymentSuccessPage";
import PaymentFailPage from "./features/payment/PaymentFailPage";

/* support */
import SessionGate from "./features/session/SessionGate";
import RequestPage from "./features/request/RequestPage";
import WaitingPage from "./features/waiting/WaitingPage";
import ChatPage from "./features/chat/ChatPage";

/* my page */
import MyPageLayout from "./features/me/layout/MyPageLayout";
import MyPageHome from "./features/me/page/MyPageHome";
import MyOrders from "./features/me/page/MyOrders";
import MyOrderDetail from "./features/me/page/MyOrderDetail";
import MyProfile from "./features/me/page/MyProfile";
import MyReserveUsers from "./features/me/page/MyReserveUsers";
import ReserveUserForm from "./features/me/page/ReserveUserForm";

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
        {/* ================= 공개 ================= */}
        <Route path="/" element={<HomePage />} />

        {/* auth */}
        <Route
          path="/login"
          element={isLoggedIn ? <Navigate to="/" replace /> : <LoginPage />}
        />
        <Route path="/signup" element={<SignUpPage />} />

        {/* ================= 티켓 ================= */}
        <Route element={<TicketLayout />}>
          <Route path="/events/:id" element={<EventDetailPage />} />
        </Route>

        <Route
          path="/events/:id/reserve"
          element={isLoggedIn ? <ReservePage /> : <Navigate to="/login" replace />}
        />

        {/* ================= 결제 ================= */}
        {/* Toss redirect landing */}
        <Route
          path="/payment/confirm"
          element={isLoggedIn ? <PaymentConfirmPage /> : <Navigate to="/" replace />}
        />

        {/* 최종 성공 화면 */}
        <Route
          path="/payment/success"
          element={isLoggedIn ? <PaymentSuccessPage /> : <Navigate to="/" replace />}
        />

        <Route path="/payment/fail" element={<PaymentFailPage />} />

    
   

        {/* ================= 마이페이지 ================= */}
<Route
    path="/me"
    element={isLoggedIn ? <MyPageLayout /> : <Navigate to="/login" replace />}
  >
    <Route index element={<MyPageHome />} />
    <Route path="orders" element={<MyOrders />} />
    <Route path="orders/:orderId" element={<MyOrderDetail />} />
    <Route path="profile" element={<MyProfile />} />
    <Route path="reserve-users" element={<MyReserveUsers />} />
    <Route path="reserve-users/new" element={<ReserveUserForm />} />
    <Route path="reserve-users/:id" element={<ReserveUserForm />} />

    {/* 🔥 상담 라우트 이동: /me/support 가 기점이 됩니다 */}
    <Route path="support" element={<SessionGate />}>
      <Route index element={<Navigate to="request" replace />} /> {/* /me/support 접속 시 바로 request로 */}
      <Route path="request" element={<RequestPage />} />
      <Route path="waiting" element={<WaitingPage />} />
      <Route path="chat/:sessionId" element={<ChatPage />} />
    </Route>
  </Route>

        {/* fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </WsGate>
  );
}
