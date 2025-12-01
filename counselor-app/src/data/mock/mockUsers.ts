export interface AppUser {
  id: number;
  email: string;
  password: string;
  nickname: string;
  status: "ACTIVE" | "BLOCKED";
  created_at: string;
}

export const mockUsers: AppUser[] = [
  {
    id: 1,
    email: "user1@test.com",
    password: "hashed123",
    nickname: "홍길동",
    status: "ACTIVE",
    created_at: "2024-11-01T09:10:00Z",
  },
  {
    id: 2,
    email: "user2@test.com",
    password: "hashed123",
    nickname: "김영희",
    status: "ACTIVE",
    created_at: "2024-11-01T09:20:00Z",
  },
  {
    id: 3,
    email: "user3@test.com",
    password: "hashed123",
    nickname: "박철수",
    status: "BLOCKED",
    created_at: "2024-11-01T09:30:00Z",
  },
];
