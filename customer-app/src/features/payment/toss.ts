import { loadTossPayments } from "@tosspayments/tosspayments-sdk";

let tossPromise: Promise<any> | null = null;

export function getTossPayments() {
  if (!tossPromise) {
    tossPromise = loadTossPayments(import.meta.env.VITE_TOSS_CLIENT_KEY);
  }
  return tossPromise;
}
