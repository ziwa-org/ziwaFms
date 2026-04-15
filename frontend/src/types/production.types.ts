import { PaginationState } from './common.types';

export interface ProductionRecord {
  id: number;
  cowId: number;
  cowTagId: string;
  date: string;
  morningQuantity: number;
  eveningQuantity: number;
  totalQuantity: number;
  notes?: string;
  createdAt: string;
}

export interface ProductionListParams {
  cowId?: number;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

export interface ProductionFormData {
  cowId: number;
  date: Date;
  morningQuantity: number;
  eveningQuantity: number;
  notes: string;
}

export interface ProductionRecordData {
  cowId: number;
  date: string;
  morningQuantity: number;
  eveningQuantity: number;
  notes?: string;
}

export interface ProductionTrend {
  date: string;
  totalProduction: number;
  averagePerCow: number;
  recordCount: number;
}

export interface CowProductivity {
  cowId: number;
  cowTagId: string;
  averageProduction: number;
}

export interface TopProducer {
  cowId: number;
  cowTagId: string;
  totalProduction: number;
  averageProduction: number;
  recordCount: number;
}

export interface ProductionState {
  records: ProductionRecord[];
  trends: ProductionTrend[];
  topProducers: TopProducer[];
  filters: ProductionListParams;
  pagination: PaginationState;
  isLoading: boolean;
  error: string | null;
}
