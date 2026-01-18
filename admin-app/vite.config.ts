import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  define: {
    // SockJS 등 실시간 통신 라이브러리 호환성을 위해 유지합니다.
    global: "window", 
    // 🔥 빌드 시 운영 서버 IP 주소와 HQ Admin 서비스 포트(8082)를 심어줍니다.
    "process.env.VITE_API_BASE_URL": JSON.stringify("http://13.209.214.254:8082"),
  },
  server: {
    port: 5173, 
    proxy: {
      "/api": {
        // 로컬 개발 환경용 설정
        target: "http://13.209.214.254:8082",
        changeOrigin: true,
        secure: false,
      },
    },
  },
  build: {
    outDir: "dist",
    chunkSizeWarningLimit: 1000,
  }
});