import { useState, useEffect } from 'react';
import { analyticsService } from '../../services/analyticsService';
import { DashboardSummary, HerdComposition } from '../../types/analytics.types';
import { MonthlyTrend } from '../../types/financial.types';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../../components/ui/card';
import { Alert, AlertDescription } from '../../components/ui/alert';
import { Button } from '../../components/ui/button';
import { Skeleton } from '../../components/ui/skeleton';
import { AlertCircle, TrendingUp, Milk, DollarSign, Activity, RefreshCw } from 'lucide-react';
import { formatCurrency, formatNumber } from '../../utils/formatters';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';

const COLORS = ['#22C55E', '#10B981', '#059669', '#047857', '#065F46', '#064E3B'];

export function AnalyticsPage() {
  const [data, setData] = useState<DashboardSummary | null>(null);
  const [herdComposition, setHerdComposition] = useState<HerdComposition[]>([]);
  const [financialTrends, setFinancialTrends] = useState<MonthlyTrend[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchAnalytics = async () => {
    try {
      setIsLoading(true);
      setError(null);
      
      // Fetch dashboard summary - don't fail if no data
      try {
        const summary = await analyticsService.getDashboardSummary();
        setData(summary);
      } catch (err) {
        console.log('No dashboard data available yet');
        setData(null);
      }
      
      // Fetch herd composition - don't fail if no data
      try {
        const composition = await analyticsService.getHerdComposition();
        setHerdComposition(composition);
      } catch (err) {
        console.log('No herd composition data available yet');
        setHerdComposition([]);
      }
      
      // Fetch financial trends for last 6 months - don't fail if no data
      try {
        const endDate = new Date();
        const startDate = new Date();
        startDate.setMonth(startDate.getMonth() - 6);
        const trends = await analyticsService.getFinancialTrends(
          startDate.toISOString().split('T')[0],
          endDate.toISOString().split('T')[0]
        );
        setFinancialTrends(trends);
      } catch (err) {
        console.log('No financial trends data available yet');
        setFinancialTrends([]);
      }
    } catch (err: any) {
      // Only set error for critical failures
      console.error('Analytics fetch error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAnalytics();
  }, []);

  // Don't show error screen, just display zero metrics if data fails to load

  return (
    <div className="min-h-screen" style={{ backgroundColor: '#f8faf9' }}>
      <div className="p-6 space-y-6">
        {/* Header Section */}
        <div className="flex flex-col space-y-3">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-semibold text-foreground">
                Analytics Dashboard
              </h1>
              <p className="text-muted-foreground mt-1">
                Farm performance metrics and insights
              </p>
            </div>
            <Button
              variant="default"
              size="sm"
              onClick={fetchAnalytics}
              disabled={isLoading}
              className="shadow-sm"
            >
              <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
              Refresh
            </Button>
          </div>
        </div>

        {/* Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {/* Active Cows Card */}
          <Card className="bg-gradient-to-br from-primary/10 to-primary/5 hover:shadow-lg transition-all hover:scale-[1.02]">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-foreground">Active Cows</CardTitle>
              <div className="h-10 w-10 rounded-full bg-white flex items-center justify-center shadow-sm">
                <Milk className="h-5 w-5 text-primary" />
              </div>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <Skeleton className="h-8 w-20" />
              ) : (
                <div className="text-2xl font-bold text-foreground">{data?.activeCowsCount ?? 0}</div>
              )}
              <p className="text-xs text-muted-foreground mt-1">
                Total herd size
              </p>
            </CardContent>
          </Card>

          {/* Today's Production Card */}
          <Card className="bg-gradient-to-br from-primary/10 to-primary/5 hover:shadow-lg transition-all hover:scale-[1.02]">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-foreground">Today's Production</CardTitle>
              <div className="h-10 w-10 rounded-full bg-white flex items-center justify-center shadow-sm">
                <Activity className="h-5 w-5 text-primary" />
              </div>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <Skeleton className="h-8 w-24" />
              ) : (
                <div className="text-2xl font-bold text-foreground">
                  {formatNumber(data?.todayProduction ?? 0)} L
                </div>
              )}
              <p className="text-xs text-muted-foreground mt-1">
                Milk collected today
              </p>
            </CardContent>
          </Card>

          {/* Monthly Profit Card */}
          <Card className="bg-gradient-to-br from-primary/10 to-primary/5 hover:shadow-lg transition-all hover:scale-[1.02]">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-foreground">Monthly Profit</CardTitle>
              <div className="h-10 w-10 rounded-full bg-white flex items-center justify-center shadow-sm">
                <DollarSign className="h-5 w-5 text-primary" />
              </div>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <Skeleton className="h-8 w-28" />
              ) : (
                <div className="text-2xl font-bold text-foreground">
                  {formatCurrency(data?.monthlyFinancialSummary?.netProfit ?? 0)}
                </div>
              )}
              <p className="text-xs text-muted-foreground mt-1">
                Net profit this month
              </p>
            </CardContent>
          </Card>

          {/* Cows in Withdrawal Card */}
          <Card className="bg-gradient-to-br from-primary/10 to-primary/5 hover:shadow-lg transition-all hover:scale-[1.02]">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-foreground">In Withdrawal</CardTitle>
              <div className="h-10 w-10 rounded-full bg-white flex items-center justify-center shadow-sm">
                <AlertCircle className="h-5 w-5 text-primary" />
              </div>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <Skeleton className="h-8 w-16" />
              ) : (
                <div className="text-2xl font-bold text-foreground">{data?.cowsInWithdrawal ?? 0}</div>
              )}
              <p className="text-xs text-muted-foreground mt-1">
                Cows under treatment
              </p>
            </CardContent>
          </Card>
        </div>

        {/* Charts Section */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Production Trends Line Chart */}
          <Card className="bg-white">
            <CardHeader>
              <CardTitle>Production Trends</CardTitle>
              <CardDescription>Daily milk production over the last 30 days</CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <Skeleton className="h-64 w-full" />
              ) : data?.productionTrend30Days && data.productionTrend30Days.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={data.productionTrend30Days}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis
                      dataKey="date"
                      tickFormatter={(value) => {
                        const date = new Date(value);
                        return `${date.getMonth() + 1}/${date.getDate()}`;
                      }}
                    />
                    <YAxis />
                    <Tooltip
                      labelFormatter={(value) => new Date(value).toLocaleDateString()}
                      formatter={(value: number) => [`${formatNumber(value)} L`, 'Production']}
                    />
                    <Legend />
                    <Line
                      type="monotone"
                      dataKey="totalProduction"
                      stroke="#22C55E"
                      strokeWidth={2}
                      name="Total Production (L)"
                      dot={{ fill: '#22C55E' }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              ) : (
                <div className="h-64 flex items-center justify-center text-muted-foreground">
                  <div className="text-center">
                    <TrendingUp className="h-12 w-12 mx-auto mb-2 text-muted-foreground/50" />
                    <p>No production data available</p>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Herd Composition Pie Chart */}
          <Card className="bg-white">
            <CardHeader>
              <CardTitle>Herd Composition</CardTitle>
              <CardDescription>Distribution of cattle by breed</CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <Skeleton className="h-64 w-full" />
              ) : herdComposition.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={herdComposition}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={(entry: any) =>
                        `${entry.breed}: ${entry.count} (${(entry.percent * 100).toFixed(0)}%)`
                      }
                      outerRadius={80}
                      fill="#8884d8"
                      dataKey="count"
                    >
                      {herdComposition.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip formatter={(value: number) => [`${value} cows`, 'Count']} />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <div className="h-64 flex items-center justify-center text-muted-foreground">
                  <div className="text-center">
                    <Milk className="h-12 w-12 mx-auto mb-2 text-muted-foreground/50" />
                    <p>No herd data available</p>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Financial Trends Bar Chart */}
        <Card className="bg-white">
          <CardHeader>
            <CardTitle>Financial Trends</CardTitle>
            <CardDescription>Monthly income, expenses, and profit over the last 6 months</CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <Skeleton className="h-80 w-full" />
            ) : financialTrends.length > 0 ? (
              <ResponsiveContainer width="100%" height={350}>
                <BarChart data={financialTrends}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip formatter={(value: number) => formatCurrency(value)} />
                  <Legend />
                  <Bar dataKey="totalIncome" fill="#22C55E" name="Income" />
                  <Bar dataKey="totalExpenses" fill="#EF4444" name="Expenses" />
                  <Bar dataKey="netProfit" fill="#3B82F6" name="Net Profit" />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="h-80 flex items-center justify-center text-muted-foreground">
                <div className="text-center">
                  <DollarSign className="h-12 w-12 mx-auto mb-2 text-muted-foreground/50" />
                  <p>No financial data available</p>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
