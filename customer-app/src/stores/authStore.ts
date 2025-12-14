import { create } from "zustand";

type AuthState = {
  counselorId: number | null;
  accessToken: string | null;
  role: "COUNSELOR" | null;
  login: (id: number, token: string) => void;
  logout: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  counselorId: null,
  accessToken: null,
  role: "COUNSELOR",

  login: (id, token) =>
    set({
      counselorId: id,
      accessToken: token,
      role: "COUNSELOR",
    }),

  logout: () =>
    set({
      counselorId: null,
      accessToken: null,
      role: null,
    }),
}));
