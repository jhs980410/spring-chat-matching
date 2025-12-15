// features/providers/WSProvider.tsx
import { useEffect, useRef, useState } from "react";
import { WSContext } from "./WSContext";
import { WSClient } from "../../ws/wsClient";
import { useAuthStore } from "../../stores/authStore";

type Props = {
  children: React.ReactNode;
};

export function WSProvider({ children }: Props) {
  const clientRef = useRef<WSClient | null>(null);
  const connectingRef = useRef(false); // 🔒 StrictMode 보호
  const [connected, setConnected] = useState(false);
console.log("[WSProvider] render");
  const accessToken = useAuthStore((s) => s.accessToken);

  // ===============================
  // 1. connect / disconnect
  // ===============================
  useEffect(() => {
    if (!accessToken) return;
    if (connectingRef.current) return; // 🔥 중복 연결 방지
     console.log("[WSProvider] accessToken =", accessToken);
    connectingRef.current = true;

    const client = new WSClient();
    clientRef.current = client;

    client.connect(
      () => {
        console.log("[WSProvider] connected");
        setConnected(true);
      },
      (err) => {
        console.error("[WSProvider] connection error", err);
        setConnected(false);
      }
    );

    return () => {
      connectingRef.current = false;
      client.disconnect();
      clientRef.current = null;
      setConnected(false);
      console.log("[WSProvider] disconnected");
    };
  }, [accessToken]);

  // ===============================
  // 2. subscribe wrapper
  // ===============================
  const subscribe = (
    destination: string,
    callback: (message: any) => void
  ) => {
    if (!clientRef.current || !connected) {
      console.warn("[WSProvider] subscribe ignored (not connected)");
      return () => {};
    }

    return clientRef.current.subscribe(destination, callback) ?? (() => {});
  };

  // ===============================
  // 3. send wrapper
  // ===============================
  const send = (destination: string, payload: any) => {
    if (!clientRef.current || !connected) {
      console.warn("[WSProvider] send ignored (not connected)");
      return;
    }
    clientRef.current.send(destination, payload);
  };

  return (
    <WSContext.Provider
      value={{
        connected,
        subscribe,
        send,
      }}
    >
      {children}
    </WSContext.Provider>
  );
}
