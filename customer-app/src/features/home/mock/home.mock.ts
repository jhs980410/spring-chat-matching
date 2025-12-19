// features/home/mock/home.mock.ts

export type Category =
  | "MUSICAL"
  | "CONCERT"
  | "SPORTS"
  | "EXHIBITION"
  | "THEATER";

export interface EventSummary {
  id: number; // = eventId
  title: string;
  category: Category;
  thumbnail: string;
  badge?: "HOT" | "NEW" | "OPEN_SOON" | null;
  openDate?: string | null;
  ranking?: number | null;
}

export const homeMock = {
  heroBanners: [
    {
      id: 1,
      title: "인기 공연 최대 할인",
      subtitle: "놓치면 끝",
      imageUrl: "/images/banner1.jpg",
    },
    {
      id: 2,
      title: "연말 콘서트 오픈",
      subtitle: "지금 예매하세요",
      imageUrl: "/images/banner2.jpg",
    },
  ],

  featuredEvents: [
    {
      id: 1,
      title: "2025 아이유 콘서트",
      category: "MUSICAL",
      thumbnail: "/images/1.png",
      badge: "HOT",
      ranking: null,
      openDate: null,
    },
    {
      id: 2,
      title: "뮤지컬 레미제라블",
      category: "MUSICAL",
      thumbnail: "/images/2.png",
      badge: "HOT",
      ranking: null,
      openDate: null,
    },
    {
      id: 3,
      title: "재즈 페스티벌",
      category: "MUSICAL",
      thumbnail: "/images/3.png",
      badge: "HOT",
      ranking: null,
      openDate: null,
    },
  ] as EventSummary[],

  rankings: {
    MUSICAL: [
      {
        id: 1,
        title: "2025 아이유 콘서트",
        category: "MUSICAL",
        thumbnail: "/images/1.png",
        ranking: 1,
        badge: null,
        openDate: null,
      },
      {
        id: 2,
        title: "뮤지컬 레미제라블",
        category: "MUSICAL",
        thumbnail: "/images/2.png",
        ranking: 2,
        badge: null,
        openDate: null,
      },
      {
        id: 3,
        title: "재즈 페스티벌",
        category: "MUSICAL",
        thumbnail: "/images/3.png",
        ranking: 3,
        badge: null,
        openDate: null,
      },
    ],
    CONCERT: [],
    SPORTS: [],
    EXHIBITION: [],
    THEATER: [],
  } as Record<Category, EventSummary[]>,

  openSoonEvents: [
    {
      id: 7,
      title: "임영웅 리사이클",
      category: "CONCERT",
      thumbnail: "/images/5.png",
      openDate: "2025-12-24",
    },
    {
      id: 8,
      title: "연극 〈살아있는 자를 수선하기〉",
      category: "THEATER",
      thumbnail: "/images/7.png",
      openDate: "2025-12-31",
    },
  ] as EventSummary[],
};
