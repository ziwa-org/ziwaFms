import axios from 'axios';
import { ApiError } from '../types/common.types';

/**
 * Parsed API error with message, field errors, and status code
 */
export interface ParsedApiError {
  message: string;
  fieldErrors?: Record<string, string>;
  statusCode?: number;
}

/**
 * Enhanced error handler that parses API errors and extracts field-level validation errors
 * 
 * @param error - The error object from an API call
 * @returns A user-friendly error message string
 */
export function handleApiError(error: unknown): string {
  const parsed = parseApiError(error);
  return parsed.message;
}

/**
 * Parse API error and extract both general message and field-specific errors
 * 
 * Handles multiple error response formats:
 * - Standard ErrorResponse (code, message, timestamp)
 * - ValidationErrorResponse (includes errors array with field-level errors)
 * - Network errors (no response from server)
 * - Generic JavaScript errors
 * 
 * @param error - The error object to parse
 * @returns ParsedApiError with message, optional field errors, and status code
 */
export function parseApiError(error: unknown): ParsedApiError {
  // Handle Axios errors (API responses)
  if (axios.isAxiosError(error)) {
    // Server responded with an error status
    if (error.response) {
      const statusCode = error.response.status;
      const errorData = error.response.data as ApiError;
      
      // Extract field-level validation errors if present
      const fieldErrors: Record<string, string> = {};
      if (errorData.errors && Array.isArray(errorData.errors) && errorData.errors.length > 0) {
        errorData.errors.forEach((fieldError) => {
          if (fieldError.field && fieldError.message) {
            fieldErrors[fieldError.field] = fieldError.message;
          }
        });
      }
      
      // Determine the main error message
      let message = errorData.message || 'An error occurred';
      
      // Handle specific HTTP status codes with appropriate messages
      if (statusCode === 401) {
        message = errorData.message || 'Unauthorized. Please log in again.';
      } else if (statusCode === 403) {
        message = errorData.message || 'You do not have permission to perform this action.';
      } else if (statusCode === 404) {
        message = errorData.message || 'The requested resource was not found.';
      } else if (statusCode === 409) {
        message = errorData.message || 'A conflict occurred. The resource may already exist.';
      } else if (statusCode === 422) {
        message = errorData.message || 'Business rule violation. Please check your input.';
      } else if (statusCode >= 500) {
        message = 'Server error. Please try again later.';
        // Log detailed error information for debugging
        console.error('Server error details:', {
          status: statusCode,
          code: errorData.code,
          message: errorData.message,
          timestamp: errorData.timestamp,
        });
      }
      
      return {
        message,
        fieldErrors: Object.keys(fieldErrors).length > 0 ? fieldErrors : undefined,
        statusCode,
      };
    }
    
    // Request was made but no response received (network error)
    if (error.request) {
      console.error('Network error - no response received:', error.message);
      return {
        message: 'Network error. Please check your internet connection and try again.',
        statusCode: 0,
      };
    }
  }
  
  // Handle standard JavaScript errors
  if (error instanceof Error) {
    console.error('Unexpected error:', error);
    return {
      message: error.message || 'An unexpected error occurred. Please try again.',
    };
  }
  
  // Fallback for unknown error types
  console.error('Unknown error type:', error);
  return {
    message: 'An unexpected error occurred. Please try again.',
  };
}
