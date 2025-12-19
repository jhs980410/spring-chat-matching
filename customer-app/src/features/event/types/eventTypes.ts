// features/event/types.ts
export interface TicketOption {
  ticketId: number;
  name: string;
  price: number;
  remainQuantity: number;
  soldOut: boolean;
}

export interface EventDetail {
  id: number;
  title: string;
  description: string;
  category: string;
  thumbnail: string;
  startAt: string;
  endAt: string;
  status: "OPEN" | "SOLD_OUT" | "CLOSED";
  ticketOptions: TicketOption[];
}
