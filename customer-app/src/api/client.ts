// src/api/client.ts
import api from "../api/axios";

export const apiClient = api.create({
  baseURL: "/api",
  withCredentials: true,
});

// 요청마다 토큰 헤더 설정 등 추가 가능
apiClient.interceptors.request.use((config) => {
  // config.headers.Authorization = `Bearer ${token}`;
  return config;
});
