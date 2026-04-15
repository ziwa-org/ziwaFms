import apiClient from './api';
import {
  HealthRecord,
  HealthListParams,
  HealthRecordData,
  WithdrawalInfo,
} from '../types/health.types';
import { PagedResponse } from '../types/common.types';

export const healthService = {
  async getHealthRecords(params: HealthListParams): Promise<PagedResponse<HealthRecord>> {
    const response = await apiClient.get<PagedResponse<HealthRecord>>('/api/health', { params });
    return response.data;
  },

  async getHealthRecordById(id: number): Promise<HealthRecord> {
    const response = await apiClient.get<HealthRecord>(`/api/health/${id}`);
    return response.data;
  },

  async createHealthRecord(data: HealthRecordData): Promise<HealthRecord> {
    const response = await apiClient.post<HealthRecord>('/api/health', data);
    return response.data;
  },

  async updateHealthRecord(id: number, data: HealthRecordData): Promise<HealthRecord> {
    const response = await apiClient.put<HealthRecord>(`/api/health/${id}`, data);
    return response.data;
  },

  async deleteHealthRecord(id: number): Promise<void> {
    await apiClient.delete(`/api/health/${id}`);
  },

  async getActiveWithdrawals(): Promise<WithdrawalInfo[]> {
    const response = await apiClient.get<WithdrawalInfo[]>('/api/health/withdrawals/active');
    return response.data;
  },
};
