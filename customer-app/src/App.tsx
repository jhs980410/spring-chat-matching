import { Routes, Route, Navigate } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "./api/axios"; // withCredentials: true 설정된 axios
import { useAuthStore } from "./stores/authStore";

import LoginPage from "./features/login/LoginPage";
import RequestPage from "./features/request/RequestPage";
import WaitingPage from "./features/waiting/WaitingPage";
import ChatPage from "./features/chat/ChatPage";
import SessionGate from "./features/session/SessionGate";
import HomePage from "./features/home/HomePage";
import WsGate from "./ws/WsGate";

export default function App() {
  const login = useAuthStore((s) => s.login);
  const logout = useAuthStore((s) => s.logout);
  const role = useAuthStore((s) => s.role); 
  
  const [isInitialized, setIsInitialized] = useState(false);

  // 🔹 세션 복구 전/후 모두 대응하도록 role 존재 여부로 로그인 판단
  const isLoggedIn = !!role; 

  useEffect(() => {
    const initAuth = async () => {
      try {
        // 1. 서버의 /me 엔드포인트 호출 (쿠키 자동 전송)
        const res = await api.get("/auth/me");
        
        // 2. 🔥 이미지(image_b0b8c6.png) 에러 해결:
        // authStore의 login 함수가 요구하는 3개의 인자(id, token, role)를 모두 전달합니다.
        // 서버 응답 데이터에 맞춰 순서대로 넣으세요.
        login(res.data.id, res.data.accessToken, res.data.role); 
        
        console.log("세션 복구 성공:", res.data.role);
      } catch (err) {
        console.log("세션 없음 또는 만료");
        logout(); // 실패 시 스토어 초기화
      } finally {
        // 3. 확인이 끝나야만 라우팅을 시작 (로그인 페이지 튕김 방지)
        setIsInitialized(true);
      }
    };

    initAuth();
  }, [login, logout]);

  // 🔹 초기화 전에는 아무것도 렌더링하지 않음
  if (!isInitialized) return null;

  return (
    <WsGate>
      <Routes>
        {/* 🌐 메인 페이지 */}
        <Route path="/" element={<HomePage />} />

        {/* 🔐 로그인: 이미 로그인됐다면 메인으로 보냄 */}
        <Route 
          path="/login" 
          element={isLoggedIn ? <Navigate to="/" replace /> : <LoginPage />} 
        />

        {/* 💬 상담 플로우: 로그인 안됐으면 로그인창으로 */}
        <Route 
          path="/support" 
          element={isLoggedIn ? <SessionGate /> : <Navigate to="/login" replace />}
        >
          <Route path="request" element={<RequestPage />} />
          <Route path="waiting" element={<WaitingPage />} />
          <Route path="chat/:sessionId" element={<ChatPage />} />
        </Route>

        {/* 없는 경로 */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </WsGate>
  );
}