// src/api/client.ts
import api from "../api/axios";

export const apiClient = api.create({
   baseURL: "http://13.209.214.254:8080/api",
  withCredentials: true,
});

// 요청마다 토큰 헤더 설정 등 추가 가능
apiClient.interceptors.request.use((config) => {
  // config.headers.Authorization = `Bearer ${token}`;
  return config;
});
