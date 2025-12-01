export interface ChatMessage {
  id: number;
  session_id: number;
  sender_type: "USER" | "COUNSELOR";
  sender_id: number;
  message: string;
  created_at: string;
}

export const mockMessages: ChatMessage[] = [
  {
    id: 1,
    session_id: 1,
    sender_type: "USER",
    sender_id: 1,
    message: "안녕하세요. 결제가 안돼요.",
    created_at: "2024-11-01T10:02:10Z",
  },
  {
    id: 2,
    session_id: 1,
    sender_type: "COUNSELOR",
    sender_id: 1,
    message: "확인 도와드리겠습니다.",
    created_at: "2024-11-01T10:02:20Z",
  },
];
