import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || '';

const api = axios.create({
  baseURL: `${API_URL}/api`,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor to add JWT token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor to handle 401 responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth endpoints
export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
};

// Maintenance CRUD endpoints
export const manutencaoApi = {
  listar: (params) => api.get('/manutencoes', { params }),
  buscar: (id) => api.get(`/manutencoes/${id}`),
  criar: (data) => api.post('/manutencoes', data),
  atualizar: (id, data) => api.put(`/manutencoes/${id}`, data),
  deletar: (id) => api.delete(`/manutencoes/${id}`),
};

// Vehicle endpoints
export const veiculoApi = {
  listar: () => api.get('/veiculos'),
  buscar: (id) => api.get(`/veiculos/${id}`),
};

// Dashboard endpoints
export const dashboardApi = {
  getData: () => api.get('/dashboard'),
  getKmVeiculo: (id) => api.get(`/dashboard/km/${id}`),
};

export default api;
