// features/providers/WebSocketProvider.tsx
import { useEffect, useRef, useState } from "react";
import { notifications } from "@mantine/notifications";
import { useNavigate } from "react-router-dom";

import SockJS from "sockjs-client";
import Stomp, { Client } from "stompjs";

import { WSContext } from "./WSContext";

export default function WebSocketProvider({
  user,
  children,
}: {
  user: { id: number; token: string | null } | null;
  children: React.ReactNode;
}) {
  const navigate = useNavigate();

  const clientRef = useRef<Client | null>(null);
  const [client, setClient] = useState<Client | null>(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    if (!user?.id || !user?.token) {
      console.log("[WS] user not ready");
      return;
    }

    console.log("[WS] Connecting counselor:", user.id);

    // 🔥 핵심 1: SockJS는 함수 호출 형태
    const socket = SockJS("http://localhost:8080/ws/connect");

    // 🔥 핵심 2: Stomp.client ❌ → Stomp.over(socket) ⭕
    const stomp: Client = Stomp.over(socket);

    // (선택) 콘솔 로그 줄이기
    stomp.debug = () => {};

    clientRef.current = stomp;

    stomp.connect(
      { Authorization: `Bearer ${user.token}` },
      () => {
        console.log("[WS] CONNECTED (COUNSELOR)");
        setClient(stomp);
        setConnected(true);

        // 🔔 상담사 전용 알림 구독
        const topic = `/sub/counselor/${user.id}`;
        stomp.subscribe(topic, (msg) => {
          const data = JSON.parse(msg.body);

          if (data.type === "MATCH_ASSIGNED") {
            notifications.show({
              title: "새 상담 배정",
              message: `세션 #${data.sessionId}`,
            });

            navigate(`/chat/${data.sessionId}`);
          }
        });
      },
      (err) => {
        console.error("[WS] CONNECTION ERROR:", err);
        setConnected(false);
        setClient(null);
      }
    );

    return () => {
      console.log("[WS] Disconnecting counselor WS...");
      if (clientRef.current) {
        try {
          clientRef.current.disconnect(() => {
            console.log(">>> DISCONNECT");
          });
        } catch (e) {
          console.warn("[WS] disconnect skipped (not connected)");
        }
        clientRef.current = null;
      }
      setClient(null);
      setConnected(false);
    };
  }, [user?.id, user?.token, navigate]);

  return (
    <WSContext.Provider
      value={{
        client,
        connected,
      }}
    >
      {children}
    </WSContext.Provider>
  );
}
