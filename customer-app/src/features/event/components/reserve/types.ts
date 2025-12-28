export type SeatStatus = "AVAILABLE" | "LOCKED" | "SOLD";


export interface Seat {
  seatId: number;
  rowLabel: string;
  seatNumber: number;
  status: SeatStatus;
}

export interface Section {
  sectionId: number;
  code: string;
  name: string;
  grade: string;
  price: number;
  totalSeats: number;
  remainSeats: number;
  seats: Seat[];
}
