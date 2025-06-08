// axiosConfig.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
});

api.interceptors.request.use((config) => {
  const rawToken = localStorage.getItem("token");
  const token = rawToken?.replace(/\s/g, '');

  console.log("📦 Token po czyszczeniu:", token);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default api;
