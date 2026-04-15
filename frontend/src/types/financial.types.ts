import { PaginationState } from './common.types';

export type TransactionType = 'INCOME' | 'EXPENSE';

export interface Transaction {
  id: number;
  date: string;
  type: TransactionType;
  category: string;
  amount: number;
  description: string;
  referenceId?: string;
  deleted: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface TransactionListParams {
  type?: TransactionType;
  category?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

export interface TransactionFormData {
  date: Date;
  type: TransactionType;
  category: string;
  amount: number;
  description: string;
  referenceId: string;
}

export interface TransactionData {
  date: string;
  type: TransactionType;
  category: string;
  amount: number;
  description: string;
  referenceId?: string;
}

export interface ProfitLossAnalysis {
  startDate: string;
  endDate: string;
  totalIncome: number;
  totalExpenses: number;
  netProfit: number;
  profitMargin: number;
}

export interface CategoryBreakdown {
  category: string;
  total: number;
  percentage: number;
  transactionCount: number;
}

export interface MonthlyTrend {
  month: string;
  totalIncome: number;
  totalExpenses: number;
  netProfit: number;
}

export interface FinancialState {
  transactions: Transaction[];
  profitLoss: ProfitLossAnalysis | null;
  incomeBreakdown: CategoryBreakdown[];
  expenseBreakdown: CategoryBreakdown[];
  trends: MonthlyTrend[];
  filters: TransactionListParams;
  dateRange: { startDate: string; endDate: string };
  pagination: PaginationState;
  isLoading: boolean;
  error: string | null;
}
