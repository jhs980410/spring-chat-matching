export interface CounselorSkill {
  id: number;
  counselor_id: number;
  category_id: number;
  created_at: string;
}

export const mockCounselorSkills: CounselorSkill[] = [
  {
    id: 1,
    counselor_id: 1,
    category_id: 1, // 결제
    created_at: "2024-11-01T09:00:00Z",
  },
  {
    id: 2,
    counselor_id: 1,
    category_id: 3, // 배송
    created_at: "2024-11-01T09:10:00Z",
  },
  {
    id: 3,
    counselor_id: 2,
    category_id: 2, // 환불
    created_at: "2024-11-01T09:20:00Z",
  },
];
