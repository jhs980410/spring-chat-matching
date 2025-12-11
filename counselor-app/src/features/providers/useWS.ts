import { useContext } from "react";
import { WebSocketContext } from "./WebSocketContext";

export const useWS = () => useContext(WebSocketContext);
