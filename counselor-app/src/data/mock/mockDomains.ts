export interface Domain {
  id: number;
  code: string;
  name: string;
  created_at: string;
}

export const mockDomains: Domain[] = [
  {
    id: 1,
    code: "FIN",
    name: "금융",
    created_at: "2024-11-01T10:00:00Z",
  },
  {
    id: 2,
    code: "COM",
    name: "통신",
    created_at: "2024-11-02T11:00:00Z",
  },
  {
    id: 3,
    code: "SHOP",
    name: "쇼핑",
    created_at: "2024-11-03T12:00:00Z",
  },
];
