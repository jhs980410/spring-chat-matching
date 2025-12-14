// features/providers/WebSocketProvider.tsx
import { useEffect, useState } from "react";
import { notifications } from "@mantine/notifications";
import { useNavigate } from "react-router-dom";
import * as Stomp from "stompjs";
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
  const [client, setClient] = useState<Client | null>(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    if (!user?.id || !user?.token) {
      console.log("[WS] user not ready");
      return;
    }

    console.log("[WS] Connecting with user:", user.id);

    const stomp: Client = Stomp.client("ws://localhost:8080/ws/connect");

    stomp.connect(
      { Authorization: `Bearer ${user.token}` },
      () => {
        console.log("[WS] CONNECTED");
        setClient(stomp);
        setConnected(true);

        // 상담사 알림용 구독
        const topic = `/sub/counselor/${user.id}`;
        stomp.subscribe(topic, (msg) => {
          const data = JSON.parse(msg.body);

          if (data.type === "MATCH_ASSIGNED") {
            notifications.show({
              title: "새 상담 연결",
              message: "상담이 배정되었습니다.",
            });

            navigate(`/chat/${data.sessionId}`);
          }
        });
      },
      (err) => {
        console.error("[WS] CONNECTION ERROR:", err);
        setConnected(false);
      }
    );

    return () => {
      console.log("[WS] Disconnecting...");
      stomp.disconnect(() => {
        setClient(null);
        setConnected(false);
      });
    };
  }, [user?.id, user?.token]);

  return (
    <WSContext.Provider value={{ client, connected }}>
      {children}
    </WSContext.Provider>
  );
}
