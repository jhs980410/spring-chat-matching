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

    // ❗ new SockJS() 사용 금지 → 그냥 함수 호출
    const socket = SockJS("http://localhost:8080/ws/connect");

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
