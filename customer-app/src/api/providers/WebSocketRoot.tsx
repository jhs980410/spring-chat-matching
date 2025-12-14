// WebSocketRoot.tsx
import { useAuthStore } from "../../stores/authStore";
import WebSocketProvider from "../providers/WebSocketProvider";
import App from "../../App";

export default function WebSocketRoot() {
  const counselorId = useAuthStore((s) => s.counselorId);
  const accessToken = useAuthStore((s) => s.accessToken);

  const user =
    counselorId && accessToken
      ? { id: counselorId, token: accessToken }
      : null;

  return (
    <WebSocketProvider user={user}>
      <App />
    </WebSocketProvider>
  );
}