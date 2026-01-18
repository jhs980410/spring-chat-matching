// ===============================
// SockJS / StompJS import
// ===============================

// SockJS는 default function import (new 사용 X)
import SockJS from "sockjs-client";

// StompJS는 default + 타입 따로 import
import Stomp, { type Client, type Message } from "stompjs";

import { useAuthStore } from "../stores/authStore";

export class WSClient {
  private stompClient: Client | null = null;

  // ===============================
  // CONNECT
  // ===============================
connect(onConnected?: () => void, onError?: (err: any) => void) {
  const token = useAuthStore.getState().accessToken;
  if (!token) {
    console.warn("[WS] No JWT token. Cannot connect.");
    return;
  }


  // 이렇게 하면 브라우저가 현재 접속 중인 도메인(예: counselor.jhs-platform.co.kr)의 /ws/connect로 요청을 보냅니다.
  const socket = SockJS("/ws/connect"); 

 
  // const socket = SockJS("https://counselor.jhs-platform.co.kr/ws/connect");

  // Stomp client
  this.stompClient = Stomp.over(socket);

  this.stompClient.connect(
    { Authorization: `Bearer ${token}` },
    () => {
      console.log("[WS] Connected");
      onConnected?.();
    },
    (error: any) => {
      console.error("[WS] Connection error:", error);
      onError?.(error);
    }
  );
}
  // ===============================
  // SUBSCRIBE
  // ===============================
  subscribe<T = any>(destination: string, onMessage: (msg: T) => void) {
    if (!this.stompClient) return;

    this.stompClient.subscribe(destination, (frame: Message) => {
      try {
        const parsed = JSON.parse(frame.body) as T;
        onMessage(parsed);
      } catch (err) {
        console.error("[WS] JSON parse error:", err);
      }
    });
  }

  // ===============================
  // SEND
  // ===============================
  send(destination: string, payload: unknown) {
    if (!this.stompClient) return;
    this.stompClient.send(destination, {}, JSON.stringify(payload));
  }

  // ===============================
  // DISCONNECT
  // ===============================
  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      console.log("[WS] Disconnected");
    }
  }
}

export const wsClient = new WSClient();
