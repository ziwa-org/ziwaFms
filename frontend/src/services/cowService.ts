import apiClient from './api';
import {
  Cow,
  CowListParams,
  CowRegistrationData,
  CowStatus,
  BreedingRecord,
  BreedingRecordData,
} from '../types/cow.types';
import { PagedResponse } from '../types/common.types';

export const cowService = {
  async getCows(params: CowListParams): Promise<PagedResponse<Cow>> {
    const response = await apiClient.get<PagedResponse<Cow>>('/api/cows', { params });
    return response.data;
  },

  async getCowById(id: number): Promise<Cow> {
    const response = await apiClient.get<Cow>(`/api/cows/${id}`);
    return response.data;
  },

  async createCow(data: CowRegistrationData): Promise<Cow> {
    const response = await apiClient.post<Cow>('/api/cows', data);
    return response.data;
  },

  async updateCow(id: number, data: CowRegistrationData): Promise<Cow> {
    const response = await apiClient.put<Cow>(`/api/cows/${id}`, data);
    return response.data;
  },

  async deleteCow(id: number): Promise<void> {
    await apiClient.delete(`/api/cows/${id}`);
  },

  async updateCowStatus(id: number, status: CowStatus): Promise<Cow> {
    const response = await apiClient.patch<Cow>(`/api/cows/${id}/status`, null, {
      params: { status },
    });
    return response.data;
  },

  async getBreedingRecords(cowId: number): Promise<BreedingRecord[]> {
    const response = await apiClient.get<BreedingRecord[]>(`/api/cows/${cowId}/breeding`);
    return response.data;
  },

  async addBreedingRecord(cowId: number, data: BreedingRecordData): Promise<BreedingRecord> {
    const response = await apiClient.post<BreedingRecord>(`/api/cows/${cowId}/breeding`, data);
    return response.data;
  },
};
