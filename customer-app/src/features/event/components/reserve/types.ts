export type SeatStatus = "AVAILABLE" | "SOLD" | "SELECTED";

export interface Section {
  id: number;
  code: string;     // 가, 나, 다
  floor: string;    // 1F, 2F
  ticketPrice: number;
}

export interface Seat {
  id: number;
  sectionId: number;
  row: string;      // A, B, C
  number: number;
  status: SeatStatus;
}
