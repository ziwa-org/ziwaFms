import apiClient from './api';
import { AuthResponse, LoginCredentials, RegisterData, User } from '../types/auth.types';

export const authService = {
  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/api/auth/login', credentials);
    return response.data;
  },

  async register(data: RegisterData): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/api/auth/register', data);
    return response.data;
  },

  logout(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user');
  },

  async refreshToken(): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/api/auth/refresh');
    return response.data;
  },

  async getCurrentUser(): Promise<User> {
    const response = await apiClient.get<User>('/api/auth/me');
    return response.data;
  },
};
