export interface ChatSession {
  id: number;
  user_id: number;
  counselor_id: number | null;
  domain_id: number;
  category_id: number;
  status: "WAITING" | "IN_PROGRESS" | "AFTER_CALL" | "ENDED";
  end_reason: "USER" | "COUNSELOR" | "TIMEOUT" | "ADMIN" | null;
  requested_at: string;
  assigned_at: string | null;
  started_at: string | null;
  ended_at: string | null;
  duration_sec: number;
}

export const mockSessions: ChatSession[] = [
  {
    id: 1,
    user_id: 1,
    counselor_id: 1,
    domain_id: 1,
    category_id: 1,
    status: "IN_PROGRESS",
    end_reason: null,
    requested_at: "2024-11-01T10:00:00Z",
    assigned_at: "2024-11-01T10:01:00Z",
    started_at: "2024-11-01T10:02:00Z",
    ended_at: null,
    duration_sec: 120,
  },
  {
    id: 2,
    user_id: 2,
    counselor_id: 2,
    domain_id: 1,
    category_id: 2,
    status: "ENDED",
    end_reason: "USER",
    requested_at: "2024-11-01T09:00:00Z",
    assigned_at: "2024-11-01T09:01:00Z",
    started_at: "2024-11-01T09:02:00Z",
    ended_at: "2024-11-01T09:20:00Z",
    duration_sec: 1100,
  },
];
