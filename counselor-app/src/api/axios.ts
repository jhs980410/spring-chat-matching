import axios from "axios";

const api = axios.create({
  baseURL: "/api",
  withCredentials: true,
});

let isRefreshing = false;
let queue: any[] = [];

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const originalRequest = error.config;

    // 🔹 [추가] 인증 확인이나 갱신 요청 자체에서 401 발생 시 루프 차단
    if (
      originalRequest.url?.includes("/auth/me") || 
      originalRequest.url?.includes("/auth/refresh")
    ) {
      return Promise.reject(error);
    }

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

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
        // 인터셉터 중첩 방지를 위해 일반 axios.post 사용 추천
        const { data } = await axios.post("/api/auth/refresh", null, {
          withCredentials: true,
        });

        const newToken = data.accessToken;
        api.defaults.headers.common["Authorization"] = `Bearer ${newToken}`;

        queue.forEach((cb) => cb(newToken));
        queue = [];
        isRefreshing = false;

        originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
        return api(originalRequest);
      } catch (e) {
        isRefreshing = false;
        queue = [];
        // 🔹 중요: 여기서 window.location.href를 사용하면 App.tsx와 충돌하여 무한 루프 가능성 있음
        // 세션 만료 시 자연스럽게 App.tsx의 catch로 넘어가게 reject만 하는 것이 안전합니다.
        return Promise.reject(e);
      }
    }

    return Promise.reject(error);
  }
);

export default api;