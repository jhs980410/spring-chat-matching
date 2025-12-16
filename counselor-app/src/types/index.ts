export interface SessionInfo {
  sessionId: number;
  status: string;

  userId: number;
  userName: string;
  userEmail: string;

  counselorId: number | null;

  domainId: number;
  domainName: string;

  categoryId: number;
  categoryName: string;

  requestedAt: string;
  startedAt: string | null;
  endedAt: string | null;

  durationSec: number | null;
  endReason: string | null;

  satisfactionScore: number | null;
  afterCallSec: number | null;
  feedback: string | null;
}
export interface ChatMessage {

  messageId: number;
  senderType: "USER" | "COUNSELOR";
  senderId: number;
  message: string;
  timestamp: number; // ms
}