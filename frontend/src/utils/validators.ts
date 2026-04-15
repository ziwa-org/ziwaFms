export function isValidDate(date: Date): boolean {
  return date instanceof Date && !isNaN(date.getTime());
}

export function isNotFutureDate(date: Date): boolean {
  return date <= new Date();
}

export function isPositiveNumber(value: number): boolean {
  return value > 0;
}

export function isNonNegativeNumber(value: number): boolean {
  return value >= 0;
}

export function isValidEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

export function isNotEmpty(value: string): boolean {
  return value.trim().length > 0;
}

export function isDateAfter(date1: Date, date2: Date): boolean {
  return date1 > date2;
}
