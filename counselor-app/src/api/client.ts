// src/api/client.ts
import axios from "axios";

export const apiClient = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
});

// 요청마다 토큰 헤더 설정 등 추가 가능
apiClient.interceptors.request.use((config) => {
  // config.headers.Authorization = `Bearer ${token}`;
  return config;
});
