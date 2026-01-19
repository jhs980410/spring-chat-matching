import api from "../api/axios";

export const apiClient = api;

apiClient.defaults.baseURL = "http://13.209.214.254:8080/api";
apiClient.defaults.withCredentials = true;

apiClient.interceptors.request.use((config) => {
  return config;
});

export default apiClient;