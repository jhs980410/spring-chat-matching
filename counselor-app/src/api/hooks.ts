// src/api/hooks.ts
import { useQuery } from "@tanstack/react-query";
import { apiClient } from "./client";
import { Notice, CounselorProfile, CounselorStat } from "../stores/mockData";

// 🔹 공지사항 목록 (notice 테이블)
export function useNotices() {
  return useQuery({
    queryKey: ["notices"],
    queryFn: async () => {
      const res = await apiClient.get<Notice[]>("/notices");
      return res.data;
    },
  });
}

// 🔹 공지사항 상세
export function useNotice(id: number) {
  return useQuery({
    queryKey: ["notices", id],
    queryFn: async () => {
      const res = await apiClient.get<Notice>(`/notices/${id}`);
      return res.data;
    },
    enabled: !!id,
  });
}

// 🔹 내 프로필 (counselor 테이블, JWT 기반)
export function useMyProfile() {
  return useQuery({
    queryKey: ["me", "profile"],
    queryFn: async () => {
      const res = await apiClient.get<CounselorProfile>("/counselors/me");
      return res.data;
    },
  });
}

// 🔹 상담사 일별 통계 (counselor_stats)
export function useMyStats() {
  return useQuery({
    queryKey: ["me", "stats"],
    queryFn: async () => {
      const res = await apiClient.get<CounselorStat[]>("/counselors/stats/me");
      return res.data;
    },
  });
}

// 🔹 세션별 메시지 (chat_message)
export function useSessionMessages(sessionId: number) {
  return useQuery({
    queryKey: ["sessions", sessionId, "messages"],
    queryFn: async () => {
      const res = await apiClient.get(`/messages/${sessionId}`);
      return res.data; // 실제 타입 정의 필요
    },
    enabled: !!sessionId,
  });
}
