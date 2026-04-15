import { useDashboard } from '../hooks/useDashboard';
import { MetricCard } from '../components/common/MetricCard';
import { Milk, DollarSign, Activity, AlertCircle } from 'lucide-react';
import { Badge } from '../components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { formatCurrency, formatNumber, formatDate } from '../utils/formatters';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export function DashboardPage() {
  const { data, isLoading, error } = useDashboard();

  // Don't show error, just display zero metrics if data fails to load
  // This prevents the page from showing errors when there's no data yet

  return (
    <div className="min-h-screen" style={{ backgroundColor: '#f8faf9' }}>
      <div className="p-6 space-y-6">
        {/* Header Section */}
        <div className="flex flex-col space-y-3">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-semibold text-foreground">
                Welcome to Ziwa Dairy Farm! 👋
              </h1>
              <p className="text-muted-foreground mt-1">
                Your farm management dashboard
              </p>
            </div>
            <div className="flex items-center gap-2">
              <Badge className="bg-primary text-white border-primary px-3 py-1.5 shadow-sm">
                <div className="w-2 h-2 bg-white rounded-full mr-2 animate-pulse"></div>
                Live Data
              </Badge>
            </div>
          </div>
        </div>

        {/* Metrics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <MetricCard
            title="Active Cows"
            value={data?.activeCowsCount ?? 0}
            icon={Milk}
            isLoading={isLoading}
          />
          <MetricCard
            title="Today's Production"
            value={`${formatNumber(data?.todayProduction ?? 0)} L`}
            icon={Activity}
            isLoading={isLoading}
          />
          <MetricCard
            title="Monthly Profit"
            value={formatCurrency(data?.monthlyFinancialSummary?.netProfit ?? 0)}
            icon={DollarSign}
            isLoading={isLoading}
          />
          <MetricCard
            title="In Withdrawal"
            value={data?.cowsInWithdrawal ?? 0}
            icon={AlertCircle}
            isLoading={isLoading}
          />
        </div>

        {/* Charts Section */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Production Trend Chart */}
          <Card className="bg-white">
            <CardHeader>
              <CardTitle>Production Trend (30 Days)</CardTitle>
              <CardDescription>Daily milk production over the last month</CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <div className="h-64 bg-muted animate-pulse rounded" />
              ) : data?.productionTrend30Days && data.productionTrend30Days.length > 0 ? (
                <ResponsiveContainer width="100%" height={250}>
                  <LineChart data={data.productionTrend30Days}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis 
                      dataKey="date" 
                      tickFormatter={(value) => formatDate(value, 'MMM dd')}
                      fontSize={12}
                    />
                    <YAxis fontSize={12} />
                    <Tooltip 
                      labelFormatter={(value) => formatDate(value as string, 'MMM dd, yyyy')}
                      formatter={(value: number) => [`${formatNumber(value)} L`, 'Production']}
                    />
                    <Line 
                      type="monotone" 
                      dataKey="totalProduction" 
                      stroke="#22c55e" 
                      strokeWidth={2}
                      dot={{ fill: '#22c55e' }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              ) : (
                <div className="h-64 flex items-center justify-center text-muted-foreground">
                  No production data available
                </div>
              )}
            </CardContent>
          </Card>

          {/* Top Producers */}
          <Card className="bg-white">
            <CardHeader>
              <CardTitle>Top Producers</CardTitle>
              <CardDescription>Highest producing cows</CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <div className="space-y-3">
                  {[1, 2, 3, 4, 5].map((i) => (
                    <div key={i} className="h-12 bg-muted animate-pulse rounded" />
                  ))}
                </div>
              ) : data?.topProducers && data.topProducers.length > 0 ? (
                <div className="space-y-3">
                  {data.topProducers.slice(0, 5).map((producer, index) => (
                    <div key={producer.cowId} className="flex items-center justify-between p-3 bg-secondary/50 rounded-lg">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center font-semibold text-primary">
                          {index + 1}
                        </div>
                        <div>
                          <p className="font-medium">{producer.cowTagId}</p>
                          <p className="text-sm text-muted-foreground">
                            Avg: {formatNumber(producer.averageProduction)} L/day
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="font-semibold">{formatNumber(producer.totalProduction)} L</p>
                        <p className="text-xs text-muted-foreground">Total</p>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="h-64 flex items-center justify-center text-muted-foreground">
                  No production data available
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Upcoming Vaccinations */}
        {data?.upcomingVaccinations && data.upcomingVaccinations.length > 0 && (
          <Card className="bg-white">
            <CardHeader>
              <CardTitle>Upcoming Vaccinations</CardTitle>
              <CardDescription>Scheduled vaccinations for the next 7 days</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {data.upcomingVaccinations.map((vaccination) => (
                  <div key={vaccination.cowId} className="flex items-center justify-between p-3 border rounded-lg">
                    <div>
                      <p className="font-medium">{vaccination.cowTagId}</p>
                      <p className="text-sm text-muted-foreground">{vaccination.description}</p>
                    </div>
                    <Badge variant="outline">
                      {formatDate(vaccination.vaccinationDate)}
                    </Badge>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}
