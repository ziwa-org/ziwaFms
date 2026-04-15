import { useState, useEffect } from 'react';
import { DashboardSummary } from '../types/analytics.types';
import { analyticsService } from '../services/analyticsService';
import { handleApiError } from '../utils/errorHandler';

export function useDashboard() {
  const [data, setData] = useState<DashboardSummary | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDashboard = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const summary = await analyticsService.getDashboardSummary();
      setData(summary);
    } catch (err) {
      setError(handleApiError(err));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  return { data, isLoading, error, refetch: fetchDashboard };
}
