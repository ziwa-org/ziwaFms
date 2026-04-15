import { LucideIcon } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

interface MetricCardProps {
  title: string;
  value: string | number;
  icon: LucideIcon;
  trend?: {
    value: number;
    direction: 'up' | 'down';
  };
  isLoading?: boolean;
}

export function MetricCard({ title, value, icon: Icon, trend, isLoading }: MetricCardProps) {
  if (isLoading) {
    return (
      <Card className="bg-gradient-to-br from-primary/10 to-primary/5 border-l-4 border-l-primary">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium text-foreground">{title}</CardTitle>
          <div className="h-10 w-10 rounded-full bg-white flex items-center justify-center shadow-sm">
            <Icon className="h-5 w-5 text-primary" />
          </div>
        </CardHeader>
        <CardContent>
          <div className="h-8 w-24 bg-white/50 animate-pulse rounded" />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="bg-gradient-to-br from-primary/10 to-primary/5 border-l-4 border-l-primary hover:shadow-lg transition-all hover:scale-[1.02]">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium text-foreground">{title}</CardTitle>
        <div className="h-10 w-10 rounded-full bg-white flex items-center justify-center shadow-sm">
          <Icon className="h-5 w-5 text-primary" />
        </div>
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold text-foreground">{value}</div>
        {trend && (
          <p className={`text-xs font-medium ${trend.direction === 'up' ? 'text-primary' : 'text-red-600'}`}>
            {trend.direction === 'up' ? '↑' : '↓'} {Math.abs(trend.value)}%
          </p>
        )}
      </CardContent>
    </Card>
  );
}
