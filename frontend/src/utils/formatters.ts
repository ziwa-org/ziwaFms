import { format, parseISO } from 'date-fns';

export function formatDate(date: string | Date, formatStr: string = 'MMM dd, yyyy'): string {
  try {
    const dateObj = typeof date === 'string' ? parseISO(date) : date;
    return format(dateObj, formatStr);
  } catch (error) {
    return 'Invalid date';
  }
}

export function parseDate(dateString: string): Date {
  return parseISO(dateString);
}

export function formatCurrency(amount: number | undefined | null): string {
  if (amount === undefined || amount === null) {
    return '$0.00';
  }
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);
}

export function formatNumber(value: number | undefined | null, decimals: number = 2): string {
  if (value === undefined || value === null) {
    return '0.00';
  }
  return value.toFixed(decimals);
}

export function formatPercentage(value: number | undefined | null): string {
  if (value === undefined || value === null) {
    return '0.00%';
  }
  return `${value.toFixed(2)}%`;
}

export function formatDateTime(date: string | Date): string {
  return formatDate(date, 'MMM dd, yyyy HH:mm');
}
