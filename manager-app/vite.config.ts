import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  define: {
    global: "window",
    // 🔥 빌드 시 운영 서버 IP 주소와 매니저 서비스 포트(8081)를 심어줍니다.
    "process.env.VITE_API_BASE_URL": JSON.stringify("http://13.209.214.254:8081"),
  },
  server: {
    port: 5175,
    proxy: {
      "/api": {
        // 로컬 개발 환경용 설정
        target: "http://13.209.214.254:8081",
        changeOrigin: true,
        secure: false,
      },
    },
  },
});