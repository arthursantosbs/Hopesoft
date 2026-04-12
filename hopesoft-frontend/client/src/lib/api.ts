import axios, { AxiosError, type AxiosInstance } from 'axios';
import { toast } from 'sonner';

function resolveApiUrl() {
  const configuredUrl = import.meta.env.VITE_API_URL?.trim();

  if (configuredUrl) {
    return configuredUrl.replace(/\/$/, '');
  }

  return 'http://localhost:8080';
}

const api: AxiosInstance = axios.create({
  baseURL: resolveApiUrl(),
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('hopesoft_token');

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('hopesoft_token');
      localStorage.removeItem('hopesoft_user');

      if (window.location.pathname !== '/login') {
        toast.error('Sua sessao expirou. Faca login novamente.');
        window.location.assign('/login');
      }
    } else if (error.response?.status === 403) {
      toast.error('Voce nao tem permissao para acessar este recurso.');
    } else if (error.response?.status === 500) {
      toast.error('Erro no servidor. Tente novamente mais tarde.');
    }

    return Promise.reject(error);
  }
);

export default api;
