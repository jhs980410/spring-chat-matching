import axios from "axios";

export const adminApi = axios.create({

  baseURL: "https://api.jhs-platform.co.kr",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
  // 관리자 권한 확인을 위한 쿠키 전송 설정
  withCredentials: true, 
});


export const publishEvent = async (draftId: number) => {
  // ❗ [중요] Nginx location /api/admin/ 블록에 매칭되도록 경로 수정
  const response = await adminApi.post(`/api/admin/events/publish/${draftId}`);
  return response.data;
};

/**
 * 초안 목록 조회 서비스 함수 (승인 대기 중인 목록)
 * 호출 시 주소: https://api.jhs-platform.co.kr/api/admin/drafts?status=REQUESTED
 */
export const getRequestedDrafts = async () => {
  // ❗ [중요] 경로 앞에 /api/admin 추가
  const response = await adminApi.get("/api/admin/drafts?status=REQUESTED");
  return response.data;
};

export default adminApi;