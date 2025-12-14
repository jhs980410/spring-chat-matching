import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./features/login/LoginPage";
import WaitingPage from "./features/waiting/WaitingPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/waiting" element={<WaitingPage />} />
    </Routes>
  );
}
