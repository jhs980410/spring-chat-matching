export interface CounselorStats {
  id: number;
  counselor_id: number;
  stat_date: string;
  handled_count: number;
  avg_duration_sec: number;
  avg_score: number;
  response_rate: number;
  success_rate: number;
  created_at: string;
}

export const mockStats: CounselorStats[] = [
  {
    id: 1,
    counselor_id: 1,
    stat_date: "2024-11-01",
    handled_count: 12,
    avg_duration_sec: 320,
    avg_score: 4.8,
    response_rate: 98.2,
    success_rate: 89.5,
    created_at: "2024-11-02T00:00:00Z",
  },
];
