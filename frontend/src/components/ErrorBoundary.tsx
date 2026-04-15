import React, { Component, ErrorInfo, ReactNode } from 'react';
import { AlertTriangle } from 'lucide-react';

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
  errorInfo: ErrorInfo | null;
}

/**
 * Error Boundary component that catches React component errors
 * and displays a fallback UI instead of crashing the entire app.
 * 
 * Logs error details to console for debugging.
 */
export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    };
  }

  static getDerivedStateFromError(error: Error): State {
    // Update state so the next render will show the fallback UI
    return {
      hasError: true,
      error,
      errorInfo: null,
    };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    // Log error details to console for debugging
    console.error('React Error Boundary caught an error:', {
      error: error.toString(),
      errorMessage: error.message,
      errorStack: error.stack,
      componentStack: errorInfo.componentStack,
      timestamp: new Date().toISOString(),
    });

    // Update state with error info
    this.setState({
      error,
      errorInfo,
    });
  }

  handleReset = (): void => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
    });
  };

  render(): ReactNode {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
          <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-6">
            <div className="flex items-center justify-center w-12 h-12 mx-auto bg-red-100 rounded-full">
              <AlertTriangle className="w-6 h-6 text-red-600" />
            </div>
            
            <h1 className="mt-4 text-xl font-semibold text-center text-gray-900">
              Something went wrong
            </h1>
            
            <p className="mt-2 text-sm text-center text-gray-600">
              An unexpected error occurred. Please try refreshing the page or contact support if the problem persists.
            </p>

            {/* Show error details in development mode */}
            {import.meta.env.DEV && this.state.error && (
              <div className="mt-4 p-3 bg-gray-100 rounded text-xs overflow-auto max-h-40">
                <p className="font-semibold text-gray-700">Error Details:</p>
                <p className="text-red-600 mt-1">{this.state.error.toString()}</p>
                {this.state.errorInfo && (
                  <pre className="mt-2 text-gray-600 whitespace-pre-wrap">
                    {this.state.errorInfo.componentStack}
                  </pre>
                )}
              </div>
            )}

            <div className="mt-6 flex gap-3">
              <button
                onClick={this.handleReset}
                className="flex-1 px-4 py-2 bg-primary text-white rounded-md hover:bg-primary/90 transition-colors"
              >
                Try Again
              </button>
              <button
                onClick={() => window.location.href = '/'}
                className="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors"
              >
                Go Home
              </button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
