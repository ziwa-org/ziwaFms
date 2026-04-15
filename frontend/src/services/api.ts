import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Store logout handler to be set by AuthContext
let logoutHandler: (() => void) | null = null;

// Function to set the logout handler from AuthContext
export const setLogoutHandler = (handler: () => void) => {
  logoutHandler = handler;
};

// Request interceptor to add JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for global 401 error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid - trigger logout
      if (logoutHandler) {
        // Use the logout handler from AuthContext to properly clear state
        logoutHandler();
      } else {
        // Fallback: clear storage and redirect manually
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user');
      }
      // Always redirect to login page
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
