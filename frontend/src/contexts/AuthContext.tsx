import { createContext, useContext, useState, useEffect, useCallback, ReactNode } from 'react';
import { User, AuthState, LoginCredentials, RegisterData } from '../types/auth.types';
import { authService } from '../services/authService';
import { handleApiError } from '../utils/errorHandler';
import { setLogoutHandler } from '../services/api';

interface AuthContextValue {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginCredentials) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<AuthState>({
    user: null,
    token: localStorage.getItem('jwt_token'),
    isAuthenticated: false,
    isLoading: true,
    error: null,
  });

  const logout = useCallback(() => {
    authService.logout();
    setState({
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,
    });
  }, []);

  useEffect(() => {
    // Register logout handler with API client for 401 interceptor
    setLogoutHandler(logout);

    // Check if user is already logged in
    const initAuth = async () => {
      const token = localStorage.getItem('jwt_token');
      const userStr = localStorage.getItem('user');
      
      if (token && userStr) {
        try {
          const user = JSON.parse(userStr);
          setState((prev) => ({
            ...prev,
            user,
            token,
            isAuthenticated: true,
            isLoading: false,
          }));
        } catch (error) {
          localStorage.removeItem('jwt_token');
          localStorage.removeItem('user');
          setState((prev) => ({ ...prev, isLoading: false }));
        }
      } else {
        setState((prev) => ({ ...prev, isLoading: false }));
      }
    };

    initAuth();
  }, [logout]);

  const login = async (credentials: LoginCredentials) => {
    try {
      const response = await authService.login(credentials);
      localStorage.setItem('jwt_token', response.token);
      localStorage.setItem('user', JSON.stringify(response.user));
      setState({
        user: response.user,
        token: response.token,
        isAuthenticated: true,
        isLoading: false,
        error: null,
      });
    } catch (error) {
      const errorMessage = handleApiError(error);
      setState((prev) => ({ ...prev, error: errorMessage, isLoading: false }));
      throw new Error(errorMessage);
    }
  };

  const register = async (data: RegisterData) => {
    try {
      const response = await authService.register(data);
      localStorage.setItem('jwt_token', response.token);
      localStorage.setItem('user', JSON.stringify(response.user));
      setState({
        user: response.user,
        token: response.token,
        isAuthenticated: true,
        isLoading: false,
        error: null,
      });
    } catch (error) {
      const errorMessage = handleApiError(error);
      setState((prev) => ({ ...prev, error: errorMessage, isLoading: false }));
      throw new Error(errorMessage);
    }
  };

  const refreshToken = async () => {
    try {
      const response = await authService.refreshToken();
      localStorage.setItem('jwt_token', response.token);
      localStorage.setItem('user', JSON.stringify(response.user));
      setState((prev) => ({
        ...prev,
        user: response.user,
        token: response.token,
      }));
    } catch (error) {
      logout();
      throw error;
    }
  };

  const contextValue: AuthContextValue = {
    user: state.user,
    token: state.token,
    isAuthenticated: state.isAuthenticated,
    isLoading: state.isLoading,
    login,
    register,
    logout,
    refreshToken,
  };

  return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
