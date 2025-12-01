export interface Category {
  id: number;
  domain_id: number;
  code: string;
  name: string;
  created_at: string;
}

export const mockCategories: Category[] = [
  {
    id: 1,
    domain_id: 1,
    code: "PAY",
    name: "결제 문제",
    created_at: "2024-11-01T10:10:00Z",
  },
  {
    id: 2,
    domain_id: 1,
    code: "REF",
    name: "환불 문의",
    created_at: "2024-11-01T10:20:00Z",
  },
  {
    id: 3,
    domain_id: 3,
    code: "DEL",
    name: "배송 문의",
    created_at: "2024-11-01T10:30:00Z",
  },
  {
    id: 4,
    domain_id: 2,
    code: "INT",
    name: "인터넷 장애",
    created_at: "2024-11-01T10:40:00Z",
  },
];
