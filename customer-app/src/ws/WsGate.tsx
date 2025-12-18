// src/ws/WsGate.tsx
import { useLocation } from "react-router-dom";
import { WSProvider } from "../api/providers/WSProvider";

export default function WsGate({ children }: { children: React.ReactNode }) {
  const location = useLocation();

  const enableWs =
    location.pathname.startsWith("/waiting") ||
    location.pathname.startsWith("/chat");

  if (!enableWs) {
    return <>{children}</>;
  }

  return <WSProvider>{children}</WSProvider>;
}
