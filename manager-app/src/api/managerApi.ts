import axios from "axios";

export const managerApi = axios.create({
  baseURL: "http://localhost:8081/api", 
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
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