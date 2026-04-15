import { MonthlyTrend } from './financial.types';
import { ProductionTrend, TopProducer } from './production.types';

export interface DashboardSummary {
  activeCowsCount: number;
  todayProduction: number;
  monthlyFinancialSummary: MonthlyTrend;
  cowsInWithdrawal: number;
  upcomingVaccinations: UpcomingVaccination[];
  productionTrend30Days: ProductionTrend[];
  topProducers: TopProducer[];
}

export interface UpcomingVaccination {
  cowId: number;
  cowTagId: string;
  vaccinationDate: string;
  description: string;
}

export interface ComparisonParams {
  startDate1: string;
  endDate1: string;
  startDate2: string;
  endDate2: string;
}

export interface ProductionComparison {
  period1: PeriodStats;
  period2: PeriodStats;
  comparison: ComparisonStats;
}

export interface PeriodStats {
  startDate: string;
  endDate: string;
  totalProduction: number;
  averageDaily: number;
  recordCount: number;
}

export interface ComparisonStats {
  productionChange: number;
  productionChangePercentage: number;
  averageDailyChange: number;
  averageDailyChangePercentage: number;
}

export interface HerdComposition {
  breed: string;
  count: number;
}
