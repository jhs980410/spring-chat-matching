import { create } from "zustand";

export type Role = "USER" | "COUNSELOR";

type AuthState = {
  userId: number | null;
  counselorId: number | null;
  accessToken: string | null;
  role: Role | null;

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
