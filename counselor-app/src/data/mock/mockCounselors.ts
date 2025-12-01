export interface Counselor {
  id: number;
  email: string;
  password: string;
  name: string;
  status: "ONLINE" | "BUSY" | "AFTER_CALL" | "OFFLINE";
  current_load: number;
  last_finished_at: string | null;
  created_at: string;
}

export const mockCounselors: Counselor[] = [
  {
    id: 1,
    email: "coun1@test.com",
    password: "pass",
    name: "이상담",
    status: "ONLINE",
    current_load: 1,
    last_finished_at: "2024-11-01T08:10:00Z",
    created_at: "2024-10-10T08:00:00Z",
  },
  {
    id: 2,
    email: "sc99999@naver.com",
    password: "1234",
    name: "정친절",
    status: "BUSY",
    current_load: 2,
    last_finished_at: "2024-11-01T08:20:00Z",
    created_at: "2024-10-11T09:00:00Z",
  },
  {
    id: 3,
    email: "coun3@test.com",
    password: "pass",
    name: "박전문",
    status: "OFFLINE",
    current_load: 0,
    last_finished_at: null,
    created_at: "2024-10-11T09:00:00Z",
  },
];
