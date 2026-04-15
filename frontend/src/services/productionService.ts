import apiClient from './api';
import {
  ProductionRecord,
  ProductionListParams,
  ProductionRecordData,
  ProductionTrend,
  CowProductivity,
  TopProducer,
} from '../types/production.types';
import { PagedResponse } from '../types/common.types';

export const productionService = {
  async getProductionRecords(params: ProductionListParams): Promise<PagedResponse<ProductionRecord>> {
    const response = await apiClient.get<PagedResponse<ProductionRecord>>('/api/production', { params });
    return response.data;
  },

  async getProductionById(id: number): Promise<ProductionRecord> {
    const response = await apiClient.get<ProductionRecord>(`/api/production/${id}`);
    return response.data;
  },

  async createProductionRecord(data: ProductionRecordData): Promise<ProductionRecord> {
    const response = await apiClient.post<ProductionRecord>('/api/production', data);
    return response.data;
  },

  async updateProductionRecord(id: number, data: ProductionRecordData): Promise<ProductionRecord> {
    const response = await apiClient.put<ProductionRecord>(`/api/production/${id}`, data);
    return response.data;
  },

  async deleteProductionRecord(id: number): Promise<void> {
    await apiClient.delete(`/api/production/${id}`);
  },

  async getProductionTrends(startDate: string, endDate: string): Promise<ProductionTrend[]> {
    const response = await apiClient.get<ProductionTrend[]>('/api/production/analytics/trends', {
      params: { startDate, endDate },
    });
    return response.data;
  },

  async getCowProductivity(limit?: number): Promise<CowProductivity[]> {
    const response = await apiClient.get<CowProductivity[]>('/api/production/analytics/cow-productivity', {
      params: { limit },
    });
    return response.data;
  },

  async getTopProducers(limit?: number): Promise<TopProducer[]> {
    const response = await apiClient.get<TopProducer[]>('/api/production/analytics/top-producers', {
      params: { limit },
    });
    return response.data;
  },
};
