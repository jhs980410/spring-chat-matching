// features/providers/useWS.ts
import { useContext } from "react";
import { WSContext } from "./WSContext";

export const useWS = () => {
  return useContext(WSContext);
};
