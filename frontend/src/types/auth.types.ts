export type UserRole = 'ADMIN' | 'MANAGER' | 'USER';

export interface User {
  id: number;
  username: string;
  fullName: string;
  role: UserRole;
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface RegisterData {
  username: string;
  password: string;
  fullName: string;
  role: UserRole;
}

export interface AuthResponse {
  token: string;
  type: string;
  expiresIn: number;
  user: User;
}

export interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

export interface LoginFormData {
  username: string;
  password: string;
}

export interface RegisterFormData {
  username: string;
  password: string;
  confirmPassword: string;
  fullName: string;
  role: UserRole;
}
