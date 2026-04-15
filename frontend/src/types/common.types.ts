/**
 * Paginated response wrapper for API endpoints that return lists
 */
export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

/**
 * Pagination state for table components
 */
export interface PaginationState {
  pageIndex: number;
  pageSize: number;
}

/**
 * Sort state for table components
 */
export interface SortState {
  sortBy: string;
  sortDirection: 'asc' | 'desc';
}

/**
 * Standard API error response structure from backend
 * Matches ErrorResponse.java and ValidationErrorResponse.java
 */
export interface ApiError {
  code: string;
  message: string;
  errors?: FieldError[];
  timestamp: string;
}

/**
 * Field-level validation error
 * Used in ValidationErrorResponse for form validation failures
 */
export interface FieldError {
  field: string;
  message: string;
}
