export interface TicketDraftResponse {
  id: number;
  name: string;
  price: number;
  totalQuantity: number;
}

export interface EventDraftDetailResponse {
  id: number;
  managerId: number;
  domainId: number;
  title: string;
  description: string;
  venue: string;
  startAt: string;
  endAt: string;
  thumbnail: string;
  status: 'DRAFT' | 'REJECTED' | 'APPROVED' | 'REQUESTED';
  requestedAt: string;
  createdAt: string;
  tickets: TicketDraftResponse[];
}

export interface EventDraftSummaryResponse {
  id: number;
  title: string;
  status: 'DRAFT' | 'REJECTED' | 'APPROVED' | 'REQUESTED';
  requestedAt: string;
  createdAt: string;
}

export interface ApprovalResponse {
  eventDraftId: number;
  status: 'APPROVED' | 'REJECTED';
  reason?: string;
  decidedAt: string;
}