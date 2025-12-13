// features/providers/WSContext.ts
import { createContext } from "react";
import type { Client } from "stompjs";

export type WSContextType = {
  client: Client | null;
  connected: boolean;
};

export const WSContext = createContext<WSContextType>({
  client: null,
  connected: false,
});
