import { useState, useEffect } from "react";
import { notifications } from "@mantine/notifications";
import { useNavigate } from "react-router-dom";
import * as Stomp from "stompjs";
import type { Client } from "stompjs";

import { WebSocketContext } from "./WebSocketContext";

export default function WebSocketProvider({ user, children }: any) {
  const navigate = useNavigate();
  const [client, setClient] = useState<Client | null>(null);

  useEffect(() => {
    if (!user) return;

    console.log("[WS] Connecting...");

    const stomp: Client = Stomp.client("ws://localhost:8080/ws/connect");
    // stomp.reconnect_delay = 0;  // stompjs는 타입 선언 안된 필드라서 사용하려면 (stomp as any).reconnect_delay 가능

    const headers: Record<string, string> = user.token
      ? { Authorization: `Bearer ${user.token}` }
      : {};

    stomp.connect(
      headers,
      (frame) => {
        console.log("[WS] CONNECT SUCCESS:", frame);
        setClient(stomp);

        const topic = `/sub/counselor/${user.id}`;

        stomp.subscribe(topic, (msg) => {
          console.log("[WS] PUSH:", msg.body);

          const data = JSON.parse(msg.body);

          if (data.type === "MATCH_ASSIGNED") {
            notifications.show({
              title: "새 상담 연결",
              message: "상담이 배정되었습니다!",
            });

            navigate(`/sessions/${data.sessionId}`);
          }
        });
      },
      (err) => console.error("[WS] CONNECTION ERROR:", err)
    );

    return () => {
      console.log("[WS] Disconnecting...");
      stomp.disconnect(() => console.log("[WS] Disconnected"));
    };
  }, [user]);

  return (
    <WebSocketContext.Provider value={client}>
      {children}
    </WebSocketContext.Provider>
  );
}
