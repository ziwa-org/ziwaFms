import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { ProtectedRoute } from './routes/ProtectedRoute';
import { Layout } from './components/layout/Layout';
import { LoginPage } from './pages/auth/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { LivestockPage } from './pages/livestock/LivestockPage';
import { CowDetailsPage } from './pages/livestock/CowDetailsPage';
import { ProductionPage } from './pages/production/ProductionPage';
import { HealthPage } from './pages/health/HealthPage';
import { FinancialPage } from './pages/financial/FinancialPage';
import { AnalyticsPage } from './pages/analytics/AnalyticsPage';
import { Toaster } from './components/ui/sonner';
import { ErrorBoundary } from './components/ErrorBoundary';

function App() {
  return (
    <ErrorBoundary>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            {/* Public routes */}
            <Route path="/login" element={<LoginPage />} />

            {/* Protected routes */}
            <Route element={<ProtectedRoute />}>
              <Route path="/" element={<Layout />}>
                <Route index element={<Navigate to="/dashboard" replace />} />
                <Route path="dashboard" element={<DashboardPage />} />
                <Route path="livestock" element={<LivestockPage />} />
                <Route path="livestock/:id" element={<CowDetailsPage />} />
                <Route path="production" element={<ProductionPage />} />
                <Route path="health" element={<HealthPage />} />
                <Route path="financial" element={<FinancialPage />} />
                <Route path="analytics" element={<AnalyticsPage />} />
              </Route>
            </Route>

            {/* 404 */}
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </BrowserRouter>
        <Toaster />
      </AuthProvider>
    </ErrorBoundary>
  );
}

export default App;
