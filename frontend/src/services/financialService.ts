import apiClient from './api';
import {
  Transaction,
  TransactionListParams,
  TransactionData,
  ProfitLossAnalysis,
  CategoryBreakdown,
  MonthlyTrend,
} from '../types/financial.types';
import { PagedResponse } from '../types/common.types';

export const financialService = {
  async getTransactions(params: TransactionListParams): Promise<PagedResponse<Transaction>> {
    const response = await apiClient.get<PagedResponse<Transaction>>('/api/financial/transactions', { params });
    return response.data;
  },

  async getTransactionById(id: number): Promise<Transaction> {
    const response = await apiClient.get<Transaction>(`/api/financial/transactions/${id}`);
    return response.data;
  },

  async createTransaction(data: TransactionData): Promise<Transaction> {
    const response = await apiClient.post<Transaction>('/api/financial/transactions', data);
    return response.data;
  },

  async updateTransaction(id: number, data: TransactionData): Promise<Transaction> {
    const response = await apiClient.put<Transaction>(`/api/financial/transactions/${id}`, data);
    return response.data;
  },

  async deleteTransaction(id: number): Promise<void> {
    await apiClient.delete(`/api/financial/transactions/${id}`);
  },

  async getProfitLoss(startDate: string, endDate: string): Promise<ProfitLossAnalysis> {
    const response = await apiClient.get<ProfitLossAnalysis>('/api/financial/analytics/profit-loss', {
      params: { startDate, endDate },
    });
    return response.data;
  },

  async getIncomeBreakdown(startDate: string, endDate: string): Promise<CategoryBreakdown[]> {
    const response = await apiClient.get<CategoryBreakdown[]>('/api/financial/analytics/income-breakdown', {
      params: { startDate, endDate },
    });
    return response.data;
  },

  async getExpenseBreakdown(startDate: string, endDate: string): Promise<CategoryBreakdown[]> {
    const response = await apiClient.get<CategoryBreakdown[]>('/api/financial/analytics/expense-breakdown', {
      params: { startDate, endDate },
    });
    return response.data;
  },

  async getFinancialTrends(startDate: string, endDate: string): Promise<MonthlyTrend[]> {
    const response = await apiClient.get<MonthlyTrend[]>('/api/financial/analytics/trends', {
      params: { startDate, endDate },
    });
    return response.data;
  },
};
