// features/home/mock/home.mock.ts

export type Category =
  | "MUSICAL"
  | "CONCERT"
  | "SPORTS"
  | "EXHIBITION"
  | "THEATER";

export interface EventSummary {
  id: number;
  title: string;
  category: Category;
  thumbnail: string;
  badge?: "HOT" | "NEW" | "OPEN_SOON";
  openDate?: string;
  ranking?: number;
}

export const homeMock = {
  heroBanners: [
  {
    id: 1,
    title: "인기 뮤지컬/전시 최대 40% 할인",
    subtitle: "뮤지컬 최대 3만원 중복 할인",
    imageUrl: "/images/banner1.jpg",
  },
  {
    id: 2,
    title: "연말 콘서트 티켓 오픈",
    subtitle: "김연우 · 임영웅 · 페스티벌",
    imageUrl: "/images/banner2.jpg",
  },
  {
    id: 3,
    title: "연극 & 클래식 추천",
    subtitle: "연말 문화생활 미리 준비하세요",
    imageUrl: "/images/banner3.jpg",
  },
],

  featuredEvents: [
    {
      id: 101,
      title: "〈센과 치히로의 행방불명〉오리지널 투어 (SPIRITED AWAY)",
      category: "MUSICAL",
      thumbnail: "/images/1.png",
      badge: "HOT",
    },
    {
      id: 102,
      title: "2025 김연우 크리스마스 콘서트 〈오마이갓연우〉",
      category: "CONCERT",
      thumbnail: "/images/kim.png",
      badge: "NEW",
    },
    {
      id: 103,
      title: "연극 〈2호선세입자〉 : 지하철 생존 코미디",
      category: "THEATER",
      thumbnail: "/images/6.png",
      badge: "OPEN_SOON",
    },
  ] as EventSummary[],

  rankings: {
    MUSICAL: [
      { id: 201, title: "뮤지컬 데스노트", thumbnail: "/images/3.png", ranking: 1 },
      { id: 202, title: "뮤지컬 레베카", thumbnail: "/images/2.png", ranking: 2 },
      { id: 203, title: "뮤지컬 라이프오브파이", thumbnail: "/images/4.png", ranking: 3 },
    ],
    CONCERT: [],
    SPORTS: [],
    EXHIBITION: [],
    THEATER: [],
  },

  openSoonEvents: [
    {
      id: 301,
      title: "임영웅 리사이클", 
      category: "CONCERT",
      openDate: "2025-12-24",
      thumbnail: "/images/5.png",
    },
    {
      id: 302,
      title: "연극 〈살아있는 자를 수선하기〉",
      category: "THEATER",
      openDate: "2025-12-31",
      thumbnail: "/images/7.png",
    },
  ] as EventSummary[],
};
