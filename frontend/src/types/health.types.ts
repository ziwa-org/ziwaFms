import { PaginationState } from './common.types';

export type HealthRecordType = 'VACCINATION' | 'TREATMENT' | 'CHECKUP';

export interface HealthRecord {
  id: number;
  cowId: number;
  cowTagId: string;
  date: string;
  recordType: HealthRecordType;
  description: string;
  veterinarianName?: string;
  medication?: string;
  withdrawalPeriodDays?: number;
  withdrawalEndDate?: string;
  cost?: number;
  createdAt: string;
}

export interface HealthListParams {
  cowId?: number;
  recordType?: HealthRecordType;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

export interface HealthFormData {
  cowId: number;
  date: Date;
  recordType: HealthRecordType;
  description: string;
  veterinarianName: string;
  medication: string;
  withdrawalPeriodDays: number;
  cost: number;
}

export interface HealthRecordData {
  cowId: number;
  date: string;
  recordType: HealthRecordType;
  description: string;
  veterinarianName?: string;
  medication?: string;
  withdrawalPeriodDays?: number;
  cost?: number;
}

export interface WithdrawalInfo {
  cowId: number;
  cowTagId: string;
  healthRecordId: number;
  withdrawalEndDate: string;
  daysRemaining: number;
  medication: string;
}

export interface HealthState {
  records: HealthRecord[];
  activeWithdrawals: WithdrawalInfo[];
  filters: HealthListParams;
  pagination: PaginationState;
  isLoading: boolean;
  error: string | null;
}
