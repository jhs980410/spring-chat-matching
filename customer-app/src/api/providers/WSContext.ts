// features/providers/WSContext.ts
import { createContext } from "react";

export type WSContextType = {
  connected: boolean;
  subscribe: (
    destination: string,
    callback: (message: any) => void
  ) => () => void;
  send: (destination: string, payload: any) => void;
};

export const WSContext = createContext<WSContextType>({
  connected: false,
  subscribe: () => () => {},
  send: () => {},
});
