export interface CounselLog {
  id: number;
  session_id: number;
  user_id: number;
  counselor_id: number;
  domain_id: number;
  category_id: number;
  duration_sec: number;
  after_call_sec: number;
  satisfaction_score: number | null;
  feedback: string | null;
  ended_at: string | null;
  created_at: string;
}

export const mockLogs: CounselLog[] = [
  {
    id: 1,
    session_id: 2,
    user_id: 2,
    counselor_id: 2,
    domain_id: 1,
    category_id: 2,
    duration_sec: 1100,
    after_call_sec: 120,
    satisfaction_score: 5,
    feedback: "친절하게 잘 도와주셨어요.",
    ended_at: "2024-11-01T09:20:00Z",
    created_at: "2024-11-01T09:21:00Z",
  },
];
