import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  define: {
    global: "window", // SockJS 등 호환성 유지용
    // 🔥 빌드 시 운영 서버 IP 주소를 코드에 직접 심어줍니다.
    "process.env.VITE_API_BASE_URL": JSON.stringify("http://13.209.214.254:8080"),
  },
  server: {
    port: 5174,
    proxy: {
      "/api": {
        // 로컬 개발 시 타겟 주소
        target: "http://13.209.214.254:8080",
        changeOrigin: true,
        secure: false,
      },
    },
  },
});