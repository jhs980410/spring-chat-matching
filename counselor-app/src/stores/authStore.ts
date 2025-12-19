import { create } from "zustand";

// Role 타입을 정의하여 관리
export type Role = "USER" | "COUNSELOR" | "ADMIN";

type AuthState = {
  userId: number | null;
  counselorId: number | null;
  accessToken: string | null;
  role: Role | null;

  // 인자를 3개(id, token, role) 받는 구조로 통일
  login: (id: number, token: string, role: Role) => void;
  logout: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  userId: null,
  counselorId: null,
  accessToken: null,
  role: null,

  login: (id, token, role) =>
    set({
      // 역할에 따라 ID를 분리해서 저장
      userId: role === "USER" ? id : null,
      counselorId: role === "COUNSELOR" ? id : null,
      accessToken: token,
      role,
    }),

  logout: () =>
    set({
      userId: null,
      counselorId: null,
      accessToken: null,
      role: null,
    }),
}));