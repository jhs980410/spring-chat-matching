import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  define: {
    global: "window",
    // 빌드 시점에 API 주소를 전역 변수처럼 심어버립니다.
    "process.env.VITE_API_BASE_URL": JSON.stringify("http://13.209.214.254:8080"),
  },

  server: {
    proxy: {
      "/api": {
        // 로컬 개발 환경용 프록시 (npm run dev 시에만 작동)
        target: "http://13.209.214.254:8080",
        changeOrigin: true,
        secure: false,
      },
      "/ws": {
        target: "http://13.209.214.254:8080",
        ws: true,
      },
    },
  },
});