import type { Seat } from "./types";

export const seats: Seat[] = Array.from({ length: 120 }, (_, i) => ({
  id: i + 1,
  sectionId: 1,
  row: "라",
  number: i + 1,
  status: i % 8 === 0 ? "SOLD" : "AVAILABLE",
}));
