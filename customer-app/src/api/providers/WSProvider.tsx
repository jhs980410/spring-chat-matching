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
  const connectingRef = useRef(false);
  const subscriptionsRef = useRef<Map<string, () => void>>(new Map()); // 🔥 핵심
  const [connected, setConnected] = useState(false);

  const accessToken = useAuthStore((s) => s.accessToken);

  // ===============================
  // 1. connect / disconnect
  // ===============================
  useEffect(() => {
    if (!accessToken) return;
    if (connectingRef.current) return;

    connectingRef.current = true;

    const client = new WSClient();
    clientRef.current = client;

    client.connect(
      () => {
        setConnected(true);
        console.log("[WS] CONNECTED");
      },
      (err) => {
        console.error("[WS] CONNECT ERROR", err);
        setConnected(false);
      }
    );

    return () => {
      // 🔥 모든 구독 해제
      subscriptionsRef.current.forEach((unsub) => unsub());
      subscriptionsRef.current.clear();

      client.disconnect();
      clientRef.current = null;
      connectingRef.current = false;
      setConnected(false);

      console.log("[WS] DISCONNECTED");
    };
  }, [accessToken]);

  // ===============================
  // 2. subscribe (중복 차단 핵심)
  // ===============================
  const subscribe = (destination: string, callback: (message: any) => void) => {
    if (!clientRef.current || !connected) {
      console.warn("[WS] subscribe ignored (not connected)");
      return () => {};
    }

    // 🔥 이미 구독 중이면 재사용
    if (subscriptionsRef.current.has(destination)) {
      console.warn("[WS] already subscribed:", destination);
      return subscriptionsRef.current.get(destination)!;
    }

    const unsubscribe =
      clientRef.current.subscribe(destination, callback) ?? (() => {});

    subscriptionsRef.current.set(destination, unsubscribe);
    console.log("[WS] SUBSCRIBED:", destination);

    return () => {
      unsubscribe();
      subscriptionsRef.current.delete(destination);
      console.log("[WS] UNSUBSCRIBED:", destination);
    };
  };

  // ===============================
  // 3. send
  // ===============================
  const send = (destination: string, payload: any) => {
    if (!clientRef.current || !connected) return;
    clientRef.current.send(destination, payload);
  };

  return (
    <WSContext.Provider value={{ connected, subscribe, send }}>
      {children}
    </WSContext.Provider>
  );
}
