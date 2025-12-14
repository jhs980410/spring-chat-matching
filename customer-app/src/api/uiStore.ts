// src/stores/uiStore.ts
// 아주 단순한 전역 상태 (선택된 세션 / 도메인 등)
// zustand 안 쓰고 React Context로 해도 되지만, 예시는 zustand 기반

import { create } from "zustand";

type UIState = {
  // 현재 선택된 chat_session.id
  currentSessionId: number | null; // chat_session.id
  setCurrentSessionId: (id: number | null) => void;
};

export const useUIStore = create<UIState>((set) => ({
  currentSessionId: null,
  setCurrentSessionId: (id) => set({ currentSessionId: id }),
}));
