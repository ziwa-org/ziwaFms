import { PaginationState } from './common.types';

export type CowStatus = 'ACTIVE' | 'SOLD' | 'DECEASED';

export interface Cow {
  id: number;
  tagId: string;
  breed: string;
  dateOfBirth: string;
  acquisitionDate: string;
  status: CowStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CowListParams {
  status?: CowStatus;
  breed?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

export interface CowFormData {
  tagId: string;
  breed: string;
  dateOfBirth: Date;
  acquisitionDate: Date;
  status: CowStatus;
}

export interface CowRegistrationData {
  tagId: string;
  breed: string;
  dateOfBirth: string;
  acquisitionDate: string;
  status: CowStatus;
}

export interface BreedingRecord {
  id: number;
  cowId: number;
  cowTagId: string;
  breedingDate: string;
  bullId: string;
  expectedCalvingDate: string;
  actualCalvingDate?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface BreedingFormData {
  breedingDate: Date;
  bullId: string;
  expectedCalvingDate: Date;
  notes: string;
}

export interface BreedingRecordData {
  breedingDate: string;
  bullId: string;
  expectedCalvingDate: string;
  notes?: string;
}

export interface LivestockState {
  cows: Cow[];
  selectedCow: Cow | null;
  filters: CowListParams;
  pagination: PaginationState;
  isLoading: boolean;
  error: string | null;
}
