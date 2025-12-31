// features/providers/WebSocketProvider.tsx
import { useEffect, useRef, useState } from "react";
import { notifications } from "@mantine/notifications";
import { useNavigate } from "react-router-dom";

import SockJS from "sockjs-client";
import Stomp from "stompjs";
import type { Client } from "stompjs";
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
  const connectingRef = useRef(false);

  // 🔥 stompjs에는 Subscription 타입이 없으므로 구조적으로 관리
  const subscriptionRef = useRef<{ unsubscribe: () => void } | null>(null);

  const [connected, setConnected] = useState(false);

  useEffect(() => {
    if (!user?.id || !user?.token) return;
    if (connectingRef.current) return;

    connectingRef.current = true;

    console.log("[WS] Connecting counselor:", user.id);

    const socket = SockJS("/ws/connect");
    const stomp: Client = Stomp.over(socket);

    // 로그 과다 방지
    stomp.debug = () => {};

    clientRef.current = stomp;

    stomp.connect(
      { Authorization: `Bearer ${user.token}` },
      () => {
        console.log("[WS] CONNECTED (COUNSELOR)");
        setConnected(true);

        // 🔒 이미 구독 중이면 재구독 금지
        if (subscriptionRef.current) {
          console.warn("[WS] counselor topic already subscribed");
          return;
        }

        const topic = `/sub/counselor/${user.id}`;

        subscriptionRef.current = stomp.subscribe(topic, (msg) => {
          const data = JSON.parse(msg.body);

          if (data.type === "MATCH_ASSIGNED") {
            notifications.show({
              title: "새 상담 배정",
              message: `세션 #${data.sessionId}`,
            });

            navigate(`/chat/${data.sessionId}`);
          }
        });

        console.log("[WS] SUBSCRIBED:", topic);
      },
      (err) => {
        console.error("[WS] CONNECTION ERROR:", err);
        setConnected(false);
        connectingRef.current = false;
      }
    );

    return () => {
      console.log("[WS] Disconnecting counselor WS...");

      // 🔥 구독 해제
      if (subscriptionRef.current) {
        try {
          subscriptionRef.current.unsubscribe();
        } catch {}
        subscriptionRef.current = null;
      }

      // 🔥 연결 해제
      if (clientRef.current) {
        try {
          clientRef.current.disconnect(() => {
            console.log(">>> DISCONNECT");
          });
        } catch {}
        clientRef.current = null;
      }

      setConnected(false);
      connectingRef.current = false;
    };
  }, [user?.id, user?.token, navigate]);

  return (
    <WSContext.Provider
      value={{
        client: clientRef.current,
        connected,
      }}
    >
      {children}
    </WSContext.Provider>
  );
}
