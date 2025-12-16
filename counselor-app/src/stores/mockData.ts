// ======================================================================
// 📌 전역 Mock Store
//     → DB 테이블 기반 Mock 데이터를 한 파일에서 관리
// ======================================================================

// ----------------------------------------------------------------------
// 🔹 공지사항 (notice 테이블)
// ----------------------------------------------------------------------
export type Notice = {
  id: number;                 // notice.id
  title: string;              // notice.title
  content: string;            // notice.content
  imageUrl?: string | null;   // notice.image_url
  fileName?: string | null;   // notice.file_name
  fileUrl?: string | null;    // notice.file_url
  createdAt: string;          // notice.created_at
  createdBy: string;          // notice.created_by (counselor_id 매핑 가능)
};

export const mockNotices: Notice[] = [
  {
    id: 1,
    title: "시스템 점검 안내",
    content:
      "12월 2일 새벽 2시 ~ 5시 시스템 점검이 예정되어 있습니다. 이 시간 동안 상담 서비스 이용이 제한될 수 있습니다.",
    imageUrl: "https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg",
    fileName: "점검_상세_안내.pdf",
    fileUrl: "/files/maintenance-detail.pdf",
    createdAt: "2025-12-01 09:12",
    createdBy: "관리자 홍길동",
  },
  {
    id: 2,
    title: "12월 상담 운영 정책 변경",
    content:
      "12월부터 야간 상담 시간은 24시 → 22시로 변경됩니다. 자세한 내용은 첨부 파일을 참고하세요.",
    imageUrl: null,
    fileName: "12월_운영_정책.hwp",
    fileUrl: "/files/policy-202512.hwp",
    createdAt: "2025-11-28 14:30",
    createdBy: "운영팀",
  },
];

// ----------------------------------------------------------------------
// 🔹 상담사 프로필 (counselor 테이블)
// ----------------------------------------------------------------------
export type CounselorProfile = {
  id: number;              // counselor.id
  email: string;           // counselor.email
  name: string;            // counselor.name
  status: "ONLINE" | "BUSY" | "AFTER_CALL" | "OFFLINE";  // counselor.status
  currentLoad: number;     // counselor.current_load
  lastFinishedAt: string;  // counselor.last_finished_at
  createdAt: string;       // counselor.created_at
};

export const mockCounselorProfile: CounselorProfile = {
  id: 2001,
  email: "counselor@test.com",
  name: "홍길동 상담사",
  status: "ONLINE",
  currentLoad: 2,
  lastFinishedAt: "2025-12-01 15:21",
  createdAt: "2025-05-10 09:00",
};

// ----------------------------------------------------------------------
// 🔹 로그인 세션 (counselor_session 테이블 가정)
// ----------------------------------------------------------------------
export type CounselorLoginSession = {
  id: number;           // counselor_session.id
  device: string;       // counselor_session.device
  ipAddress: string;    // counselor_session.ip_address
  createdAt: string;    // counselor_session.created_at
  lastAccessAt: string; // counselor_session.last_access_at
  active: boolean;      // counselor_session.is_active
};

export const mockLoginSessions: CounselorLoginSession[] = [
  {
    id: 1,
    device: "Chrome · Windows 11",
    ipAddress: "123.45.67.89",
    createdAt: "2025-12-01 09:10",
    lastAccessAt: "2025-12-01 16:02",
    active: true,
  },
  {
    id: 2,
    device: "Edge · Windows 10",
    ipAddress: "10.0.0.21",
    createdAt: "2025-11-30 20:03",
    lastAccessAt: "2025-11-30 21:55",
    active: false,
  },
];

// ----------------------------------------------------------------------
// 🔹 상담 내역 (chat_session + app_user + counselor + category)
// ----------------------------------------------------------------------
export type SessionSummary = {
  id: number;             // chat_session.id
  requestedAt: string;    // chat_session.requested_at
  startedAt: string|null; // chat_session.started_at
  endedAt: string|null;   // chat_session.ended_at
  durationSec: number;    // chat_session.duration_sec
  endReason: string|null; // chat_session.end_reason

  userName: string;       // app_user.nickname
  userEmail: string;      // app_user.email

  counselorName: string;  // counselor.name
  domainName: string;     // domain.name
  categoryName: string;   // category.name
};

export const mockSessions: SessionSummary[] = [
  {
    id: 1,
    requestedAt: "2025-12-01 10:00",
    startedAt: "2025-12-01 10:01",
    endedAt: "2025-12-01 10:23",
    durationSec: 143,
    endReason: "USER",

    userName: "김고객",
    userEmail: "user@test.com",
    counselorName: "홍길동",
    domainName: "UNICON 쇼핑몰",
    categoryName: "배송문의",
  },
  {
    id: 2,
    requestedAt: "2025-12-01 13:21",
    startedAt: "2025-12-01 13:22",
    endedAt: null,
    durationSec: 83,
    endReason: null,

    userName: "박사용자",
    userEmail: "abc@test.com",
    counselorName: "이상담",
    domainName: "UNICON 쇼핑몰",
    categoryName: "환불/취소",
  },
];

// ----------------------------------------------------------------------
// 🔹 상담 메시지 (chat_message)
// ----------------------------------------------------------------------
export type ChatMessage = {
  id: number;   
  sessionId: number;     // chat_message.session_id
  sender: "USER" | "COUNSELOR"; // chat_message.sender_type
  senderId: number;      // chat_message.sender_id
  message: string;       // chat_message.message
  createdAt: string;     // chat_message.created_at
};



// ----------------------------------------------------------------------
// 🔹 상담사 KPI (counselor_stats 테이블)
// ----------------------------------------------------------------------
export type CounselorStat = {
  statDate: string;       // counselor_stats.stat_date
  handledCount: number;   // counselor_stats.handled_count
  avgDurationSec: number; // counselor_stats.avg_duration_sec
  avgScore: number;       // counselor_stats.avg_score
  responseRate: number;   // counselor_stats.response_rate
  successRate: number;    // counselor_stats.success_rate
};

export const mockStats: CounselorStat[] = [
  {
    statDate: "2025-11-27",
    handledCount: 12,
    avgDurationSec: 143,
    avgScore: 4.7,
    responseRate: 98.3,
    successRate: 92.1,
  },
  {
    statDate: "2025-11-28",
    handledCount: 15,
    avgDurationSec: 151,
    avgScore: 4.5,
    responseRate: 96.1,
    successRate: 90.2,
  },
  {
    statDate: "2025-11-29",
    handledCount: 9,
    avgDurationSec: 138,
    avgScore: 4.8,
    responseRate: 99.0,
    successRate: 93.4,
  },
];
