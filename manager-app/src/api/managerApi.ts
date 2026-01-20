import axios from "axios";

export const managerApi = axios.create({

  baseURL: "https://api.jhs-platform.co.kr", 
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
  // JWT 쿠키를 공유하기 위해 필수
  withCredentials: true, 
});

/**
 * 공연 초안 생성 

 */
export const createEventDraft = async (eventData: any) => {
  
  const response = await managerApi.post("/api/manager/drafts", eventData);
  return response.data;
};

/**
 * 내가 작성한 초안 목록 조회
 * 실제 요청 주소: https://api.jhs-platform.co.kr/api/drafts/my
 */
export const getMyDrafts = async () => {
  const response = await managerApi.get("/api/manager/drafts");
  return response.data;
};

export default managerApi;