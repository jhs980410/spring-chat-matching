import axios from "axios";

const api = axios.create({
  baseURL: "/api",
  withCredentials: true, // 쿠키 전송을 위해 필수
});

let isRefreshing = false;
let queue: any[] = [];

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const originalRequest = error.config;

    // 1️⃣ [중요] 인증 확인(/me)이나 토큰 갱신(/refresh) 요청 자체에서 에러가 난 경우
    // 더 이상 재시도하지 않고 에러를 던져 무한 루프를 방지합니다.
    if (
      originalRequest.url?.includes("/auth/me") || 
      originalRequest.url?.includes("/auth/refresh")
    ) {
      return Promise.reject(error);
    }

    // 2️⃣ 401(Unauthorized) 에러 발생 시 토큰 갱신 로직
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      // 이미 다른 요청이 토큰을 갱신 중이라면 큐에서 대기
      if (isRefreshing) {
        return new Promise((resolve) => {
          queue.push((token: string) => {
            originalRequest.headers["Authorization"] = `Bearer ${token}`;
            resolve(api(originalRequest));
          });
        });
      }

      isRefreshing = true;

      try {
        // 3️⃣ 리프레시 토큰으로 엑세스 토큰 재발급 요청
        // 에러 방지를 위해 baseURL이 중복되지 않도록 "/auth/refresh"만 사용 (이미 baseURL이 /api이므로)
        const { data } = await axios.post("/api/auth/refresh", null, {
          withCredentials: true,
        });

        const newToken = data.accessToken;
        
        // 새로운 토큰을 기본 헤더에 설정
        api.defaults.headers.common["Authorization"] = `Bearer ${newToken}`;

        // 대기 중이던 요청들 모두 실행
        queue.forEach((cb) => cb(newToken));
        queue = [];
        isRefreshing = false;

        // 현재 실패했던 요청 재시도
        originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
        return api(originalRequest);

      } catch (refreshError) {
        // 4️⃣ 리프레시 실패 시 (로그인 만료 등)
        isRefreshing = false;
        queue = [];
        

        // window.location.href = "/login"; 
        
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;