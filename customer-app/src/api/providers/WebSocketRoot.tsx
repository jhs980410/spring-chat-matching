// WebSocketRoot.tsx
import { useAuthStore } from "../../stores/authStore";
import { WSProvider } from "../providers/WSProvider";
import App from "../../App";

export default function WebSocketRoot() {
  const accessToken = useAuthStore((s) => s.accessToken);

  // accessToken 없으면 WS 연결 안 됨 (WSProvider 내부에서 처리)
  if (!accessToken) {
    return <App />;
  }

  return (
    <WSProvider>
      <App />
    </WSProvider>
  );
}
