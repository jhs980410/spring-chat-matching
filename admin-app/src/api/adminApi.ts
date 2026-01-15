import axios from "axios";

export const adminApi = axios.create({
  baseURL: "http://localhost:8082/api",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 운영 서버 배포(Publish) 서비스 함수
export const publishEvent = async (draftId: number) => {
  const response = await adminApi.post(`/events/publish/${draftId}`);
  return response.data;
};

// 초안 목록 조회 서비스 함수
export const getRequestedDrafts = async () => {
  const response = await adminApi.get("/drafts?status=REQUESTED");
  return response.data;
};