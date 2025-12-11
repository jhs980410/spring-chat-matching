import { createContext } from "react";
import type { Client } from "stompjs";

// WebSocket Client 타입 Context
export const WebSocketContext = createContext<Client | null>(null);
