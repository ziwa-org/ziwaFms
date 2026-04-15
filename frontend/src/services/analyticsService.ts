import apiClient from './api';
import {
  DashboardSummary,
  ComparisonParams,
  ProductionComparison,
  HerdComposition,
} from '../types/analytics.types';
import { MonthlyTrend } from '../types/financial.types';

export const analyticsService = {
  async getDashboardSummary(): Promise<DashboardSummary> {
    const response = await apiClient.get<DashboardSummary>('/api/analytics/dashboard');
    return response.data;
  },

  async getProductionComparison(params: ComparisonParams): Promise<ProductionComparison> {
    const response = await apiClient.get<ProductionComparison>('/api/analytics/production-comparison', {
      params,
    });
    return response.data;
  },

  async getHerdComposition(): Promise<HerdComposition[]> {
    // Fetch all cows and calculate breed distribution
    const response = await apiClient.get('/api/cows', {
      params: { page: 0, size: 1000 }, // Get all cows
    });
    const cows = response.data.content || [];
    
    // Group by breed and count
    const breedMap = new Map<string, number>();
    cows.forEach((cow: any) => {
      const breed = cow.breed || 'Unknown';
      breedMap.set(breed, (breedMap.get(breed) || 0) + 1);
    });
    
    // Convert to array format for pie chart
    return Array.from(breedMap.entries()).map(([breed, count]) => ({
      breed,
      count,
    }));
  },

  async getFinancialTrends(startDate: string, endDate: string): Promise<MonthlyTrend[]> {
    const response = await apiClient.get<MonthlyTrend[]>('/api/financial/analytics/trends', {
      params: { startDate, endDate },
    });
    return response.data;
  },
};
