import axios from "axios";

export const managerApi = axios.create({
  // ❗ [수정] localhost 주소를 제거하고 상대 경로를 사용합니다.
  // 이렇게 하면 Nginx가 /api 요청을 가로채서 ticket-manager-service:8081로 전달합니다.
  baseURL: "/api", 
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
  // 쿠키 기반 인증(세션/JWT)을 사용한다면 아래 옵션 추가가 필요할 수 있습니다.
  withCredentials: true, 
});

/**
 * 공연 초안 생성 
 * @param eventData - categoryId, title, venue 등과 tickets 배열 포함
 */
export const createEventDraft = async (eventData: any) => {
  const response = await managerApi.post("/drafts", eventData);
  return response.data;
};

/**
 * 내가 작성한 초안 목록 조회 (승인 요청 전 단계 확인용)
 */
export const getMyDrafts = async () => {
  const response = await managerApi.get("/drafts/my");
  return response.data;
};

export default managerApi;