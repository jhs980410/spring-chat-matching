// features/ws/WSClient.ts
import SockJS from "sockjs-client";
import Stomp, { type Client, type Message } from "stompjs";
import { useAuthStore } from "../stores/authStore";

export class WSClient {
  private stompClient: Client | null = null;

  connect(onConnected?: () => void, onError?: (err: any) => void) {
    const token = useAuthStore.getState().accessToken;
    if (!token) return;

    const socket = SockJS("/ws/connect");
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect(
      { Authorization: `Bearer ${token}` },
      () => {
        onConnected?.();
      },
      (err) => {
        onError?.(err);
      }
    );
  }

  subscribe<T = any>(
    destination: string,
    onMessage: (msg: T) => void
  ) {
    if (!this.stompClient) return;

    const sub = this.stompClient.subscribe(
      destination,
      (frame: Message) => {
        const parsed = JSON.parse(frame.body) as T;
        onMessage(parsed);
      }
    );

    return () => sub.unsubscribe();
  }

  send(destination: string, payload: any) {
    if (!this.stompClient) return;
    this.stompClient.send(destination, {}, JSON.stringify(payload));
  }

  disconnect() {
    this.stompClient?.disconnect();
    this.stompClient = null;
  }
}
