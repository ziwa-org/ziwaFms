# Tasks: Ziwa Dairy Farm Management System Frontend

## 1. Project Setup and Configuration

- [ ] 1.1 Initialize Vite React TypeScript project
  - Create new Vite project with React and TypeScript template
  - Configure TypeScript with strict mode
  - Set up project structure with folders: components, pages, hooks, services, types, utils, routes

- [ ] 1.2 Install and configure dependencies
  - Install React Router v6 for routing
  - Install Tailwind CSS and configure
  - Install shadcn/ui dependencies (@radix-ui packages)
  - Install Recharts for data visualization
  - Install Axios for HTTP requests
  - Install React Hook Form for form management
  - Install date-fns for date handling
  - Install Lucide React for icons
  - Install Sonner for toast notifications

- [ ] 1.3 Configure Tailwind CSS with custom theme
  - Set up tailwind.config.js with sidebar colors matching Energy Management Dashboard
  - Configure custom colors for primary, secondary, muted, accent, destructive
  - Add custom border radius values
  - Create global CSS file with base styles

- [ ] 1.4 Set up environment configuration
  - Create .env.example file with VITE_API_BASE_URL
  - Create .env file for local development
  - Configure Vite proxy for API requests

- [ ] 1.5 Copy shadcn/ui components from Energy Management Dashboard
  - Copy all UI components from Energy Management Dashboard/src/components/ui/
  - Ensure all Radix UI dependencies are installed
  - Test that components render correctly

## 2. Type Definitions and Interfaces

- [ ] 2.1 Create authentication type definitions
  - Define User, AuthResponse, LoginCredentials, RegisterData interfaces
  - Define UserRole type
  - Define AuthState interface

- [ ] 2.2 Create livestock type definitions
  - Define Cow, CowStatus, BreedingRecord interfaces
  - Define CowListParams, CowFormData interfaces
  - Define LivestockState interface

- [ ] 2.3 Create production type definitions
  - Define ProductionRecord, ProductionTrend, TopProducer interfaces
  - Define ProductionListParams, ProductionFormData interfaces
  - Define ProductionState interface

- [ ] 2.4 Create health type definitions
  - Define HealthRecord, HealthRecordType, WithdrawalInfo interfaces
  - Define HealthListParams, HealthFormData interfaces
  - Define HealthState interface

- [ ] 2.5 Create financial type definitions
  - Define Transaction, TransactionType, ProfitLossAnalysis interfaces
  - Define CategoryBreakdown, MonthlyTrend interfaces
  - Define TransactionListParams, TransactionFormData, FinancialState interfaces

- [ ] 2.6 Create analytics type definitions
  - Define DashboardSummary, ProductionComparison interfaces
  - Define ComparisonParams, PeriodStats, ComparisonStats interfaces

- [ ] 2.7 Create common type definitions
  - Define PagedResponse, PaginationState, SortState interfaces
  - Define ApiError, FieldError interfaces

## 3. API Service Layer

- [ ] 3.1 Create base API client configuration
  - Set up Axios instance with base URL and timeout
  - Implement request interceptor to add JWT token to headers
  - Implement response interceptor for error handling and 401 redirects
  - Export configured API client

- [ ] 3.2 Implement authentication service
  - Create authService.ts with login, register, logout, refreshToken, getCurrentUser functions
  - Implement JWT token storage in localStorage
  - Handle authentication errors

- [ ] 3.3 Implement cow service
  - Create cowService.ts with getCows, getCowById, createCow, updateCow, deleteCow functions
  - Implement updateCowStatus, getBreedingRecords, addBreedingRecord functions
  - Handle pagination and filtering parameters

- [ ] 3.4 Implement production service
  - Create productionService.ts with getProductionRecords, getProductionById functions
  - Implement createProductionRecord, updateProductionRecord, deleteProductionRecord functions
  - Implement getProductionTrends, getCowProductivity, getTopProducers functions

- [ ] 3.5 Implement health service
  - Create healthService.ts with getHealthRecords, getHealthRecordById functions
  - Implement createHealthRecord, updateHealthRecord, deleteHealthRecord functions
  - Implement getActiveWithdrawals function

- [ ] 3.6 Implement financial service
  - Create financialService.ts with getTransactions, getTransactionById functions
  - Implement createTransaction, updateTransaction, deleteTransaction functions
  - Implement getProfitLoss, getIncomeBreakdown, getExpenseBreakdown, getFinancialTrends functions

- [ ] 3.7 Implement analytics service
  - Create analyticsService.ts with getDashboardSummary function
  - Implement getProductionComparison function

## 4. Utility Functions

- [ ] 4.1 Create date formatting utilities
  - Implement formatDate function with multiple format options
  - Implement parseDate function
  - Implement date validation functions

- [ ] 4.2 Create number formatting utilities
  - Implement formatCurrency function
  - Implement formatNumber function with decimal places
  - Implement formatPercentage function

- [ ] 4.3 Create validation utilities
  - Implement form validation helper functions
  - Implement date validation (not in future, valid format)
  - Implement number validation (positive, non-negative)

- [ ] 4.4 Create data transformation utilities
  - Implement transformCowForDisplay function
  - Implement transformProductionForChart function
  - Implement transformFinancialForChart function

- [ ] 4.5 Create error handling utilities
  - Implement handleApiError function to extract user-friendly error messages
  - Implement error logging utilities

- [ ] 4.6 Create constants file
  - Define application constants (page sizes, date formats, etc.)
  - Define category options for transactions
  - Define status options for cows and health records

## 5. Authentication and Context

- [ ] 5.1 Create AuthContext and AuthProvider
  - Implement AuthContext with user, token, isAuthenticated, isLoading state
  - Implement login, register, logout, refreshToken functions
  - Handle token storage and retrieval from localStorage
  - Implement token validation on app load

- [ ] 5.2 Create useAuth custom hook
  - Export useAuth hook to access AuthContext
  - Throw error if used outside AuthProvider

- [ ] 5.3 Create ProtectedRoute component
  - Implement route protection based on authentication status
  - Redirect to login page if not authenticated
  - Show loading screen while checking authentication

- [ ] 5.4 Create route configuration
  - Set up React Router with public and protected routes
  - Configure routes for login, register, dashboard, livestock, production, health, financial, analytics
  - Implement 404 not found route

## 6. Layout Components

- [ ] 6.1 Create Sidebar component
  - Adapt Sidebar from Energy Management Dashboard
  - Update navigation items: Dashboard, Livestock, Production, Health, Financial, Analytics
  - Change icon from Zap to Milk (or appropriate dairy icon)
  - Update branding to "Ziwa Dairy" instead of "EMS Control"
  - Maintain collapsible functionality and animations
  - Update user profile section

- [ ] 6.2 Create Header component
  - Implement user greeting with full name
  - Display role badge (Admin, Manager, User)
  - Add logout button
  - Implement responsive layout

- [ ] 6.3 Create Layout component
  - Combine Sidebar and Header
  - Provide main content area with proper spacing
  - Handle responsive breakpoints
  - Implement mobile hamburger menu

- [ ] 6.4 Create LoadingScreen component
  - Design full-screen loading indicator
  - Use during initial authentication check

- [ ] 6.5 Create ErrorBoundary component
  - Implement error boundary to catch React errors
  - Display user-friendly error message
  - Provide reload button

## 7. Common Components

- [ ] 7.1 Create MetricCard component
  - Display icon, title, value, and optional trend
  - Implement gradient background with hover effects
  - Add loading skeleton state
  - Make responsive

- [ ] 7.2 Create DataTable component
  - Implement generic reusable table with column configuration
  - Add pagination controls
  - Implement sorting functionality
  - Add row click handler
  - Implement loading and empty states

- [ ] 7.3 Create FilterSection component
  - Create reusable filter controls
  - Support dropdown filters, date range pickers, text inputs
  - Implement apply and clear buttons
  - Make responsive

- [ ] 7.4 Create LoadingSkeleton component
  - Create skeleton loaders for cards, tables, charts
  - Match the shape of actual content

- [ ] 7.5 Create ConfirmDialog component
  - Implement confirmation dialog for destructive actions
  - Use shadcn/ui AlertDialog component
  - Provide customizable title, description, and actions

## 8. Dashboard Page

- [ ] 8.1 Create DashboardPage component
  - Implement page layout with header and metrics section
  - Display welcome message with user's name
  - Create grid layout for metric cards

- [ ] 8.2 Implement dashboard metrics cards
  - Create metric card for active cows count
  - Create metric card for today's production
  - Create metric card for monthly net profit
  - Create metric card for cows in withdrawal

- [ ] 8.3 Create ProductionTrendChart component
  - Implement line chart using Recharts
  - Display 30-day production trends
  - Add tooltip with date and production value
  - Make responsive

- [ ] 8.4 Create TopProducersTable component
  - Display top 5 producing cows
  - Show rank, tag ID, total production, average daily
  - Implement click to navigate to cow details

- [ ] 8.5 Create UpcomingVaccinationsCard component
  - Display list of upcoming vaccinations (next 7 days)
  - Show cow tag ID, vaccination date, description
  - Implement click to navigate to health page

- [ ] 8.6 Implement dashboard data fetching
  - Create useDashboard custom hook
  - Fetch dashboard summary from analytics service
  - Handle loading and error states
  - Implement auto-refresh every 5 minutes

## 9. Authentication Pages

- [ ] 9.1 Create LoginPage component
  - Design login form with username and password fields
  - Implement form validation with React Hook Form
  - Handle login submission
  - Display error messages
  - Add link to registration page

- [ ] 9.2 Create RegisterPage component
  - Design registration form with username, password, confirm password, full name, role fields
  - Implement form validation
  - Handle registration submission
  - Display error messages
  - Add link to login page

- [ ] 9.3 Implement authentication error handling
  - Display user-friendly error messages for invalid credentials
  - Handle rate limiting errors (429)
  - Handle network errors

## 10. Livestock Management Pages

- [ ] 10.1 Create LivestockListPage component
  - Implement page layout with header and action buttons
  - Add "Register New Cow" button
  - Display DataTable with cow list

- [ ] 10.2 Implement livestock filtering
  - Add FilterSection with status and breed filters
  - Implement filter application
  - Update table when filters change

- [ ] 10.3 Implement livestock pagination
  - Add pagination controls to DataTable
  - Handle page changes
  - Implement page size selector (10, 20, 50)

- [ ] 10.4 Implement livestock sorting
  - Add sorting to table columns (tag ID, breed, date of birth)
  - Handle sort changes

- [ ] 10.5 Create CowRegistrationForm component
  - Design form with tag ID, breed, date of birth, acquisition date, status fields
  - Implement form validation
  - Handle form submission
  - Display in dialog/modal

- [ ] 10.6 Implement cow registration
  - Open registration form dialog on button click
  - Submit form data to cow service
  - Display success message
  - Refresh cow list after successful registration

- [ ] 10.7 Create CowDetailsPage component
  - Display cow information (tag ID, breed, dates, status)
  - Show breeding records section
  - Show recent production records section
  - Show recent health records section

- [ ] 10.8 Implement cow status update
  - Add status dropdown for Managers and Admins
  - Handle status change submission
  - Display success message
  - Refresh cow details

- [ ] 10.9 Create BreedingRecordForm component
  - Design form with breeding date, bull ID, expected calving date, notes fields
  - Implement form validation
  - Handle form submission

- [ ] 10.10 Implement breeding record creation
  - Add "Add Breeding Record" button on cow details page
  - Open breeding form dialog
  - Submit form data to cow service
  - Refresh breeding records list

- [ ] 10.11 Implement cow editing
  - Add edit button on cow details page
  - Open registration form with existing data
  - Submit updated data to cow service
  - Refresh cow details

- [ ] 10.12 Implement cow deletion
  - Add delete button on cow details page (Admin only)
  - Show confirmation dialog
  - Submit delete request to cow service
  - Navigate back to livestock list

- [ ] 10.13 Create useCows custom hook
  - Implement data fetching with filters and pagination
  - Handle loading and error states
  - Provide refetch function

## 11. Production Management Page

- [ ] 11.1 Create ProductionPage component
  - Implement page layout with header and action buttons
  - Add "Record Production" button
  - Display DataTable with production records

- [ ] 11.2 Implement production filtering
  - Add FilterSection with cow selector and date range filters
  - Implement filter application
  - Update table when filters change

- [ ] 11.3 Implement production pagination and sorting
  - Add pagination controls
  - Implement sorting by date, cow, quantity

- [ ] 11.4 Create ProductionRecordForm component
  - Design form with cow selector, date picker, morning quantity, evening quantity, notes fields
  - Implement auto-calculation of total quantity
  - Implement form validation
  - Handle form submission

- [ ] 11.5 Implement production recording
  - Open production form dialog on button click
  - Fetch active cows for selector
  - Submit form data to production service
  - Display success message
  - Refresh production list

- [ ] 11.6 Implement production record editing
  - Add edit action to table rows
  - Open form with existing data
  - Submit updated data
  - Refresh production list

- [ ] 11.7 Implement production record deletion
  - Add delete action to table rows
  - Show confirmation dialog
  - Submit delete request
  - Refresh production list

- [ ] 11.8 Create ProductionAnalyticsSection component
  - Display production trends chart
  - Display top producers table
  - Display cow productivity metrics

- [ ] 11.9 Implement production analytics data fetching
  - Fetch production trends based on date range
  - Fetch top producers
  - Fetch cow productivity metrics

- [ ] 11.10 Create useProduction custom hook
  - Implement data fetching with filters and pagination
  - Handle loading and error states
  - Provide refetch function

## 12. Health Management Page

- [ ] 12.1 Create HealthPage component
  - Implement page layout with header and action buttons
  - Add "Create Health Record" button
  - Display DataTable with health records

- [ ] 12.2 Implement health filtering
  - Add FilterSection with cow selector, record type, and date range filters
  - Implement filter application
  - Update table when filters change

- [ ] 12.3 Implement health pagination and sorting
  - Add pagination controls
  - Implement sorting by date, cow, record type

- [ ] 12.4 Create HealthRecordForm component
  - Design form with cow selector, date, record type, description, veterinarian, medication, withdrawal period, cost fields
  - Implement form validation
  - Handle form submission

- [ ] 12.5 Implement health record creation
  - Open health form dialog on button click
  - Fetch active cows for selector
  - Submit form data to health service
  - Display success message
  - Refresh health records list

- [ ] 12.6 Implement health record editing
  - Add edit action to table rows
  - Open form with existing data
  - Submit updated data
  - Refresh health records list

- [ ] 12.7 Implement health record deletion
  - Add delete action to table rows
  - Show confirmation dialog
  - Submit delete request
  - Refresh health records list

- [ ] 12.8 Create WithdrawalPeriodsSection component
  - Display list of cows in withdrawal period
  - Show cow tag ID, medication, end date, days remaining
  - Highlight cows with < 3 days remaining in red

- [ ] 12.9 Implement withdrawal periods data fetching
  - Fetch active withdrawals from health service
  - Calculate days remaining
  - Sort by days remaining (ascending)

- [ ] 12.10 Create useHealth custom hook
  - Implement data fetching with filters and pagination
  - Handle loading and error states
  - Provide refetch function

## 13. Financial Management Page

- [ ] 13.1 Create FinancialPage component
  - Implement page layout with header and action buttons
  - Add "Record Transaction" button
  - Display DataTable with transactions

- [ ] 13.2 Implement financial filtering
  - Add FilterSection with type, category, and date range filters
  - Implement filter application
  - Update table when filters change

- [ ] 13.3 Implement financial pagination and sorting
  - Add pagination controls
  - Implement sorting by date, amount, type

- [ ] 13.4 Create TransactionForm component
  - Design form with date, type, category, amount, description, reference ID fields
  - Implement dynamic category dropdown based on type
  - Implement form validation
  - Handle form submission

- [ ] 13.5 Implement transaction recording
  - Open transaction form dialog on button click
  - Submit form data to financial service
  - Display success message
  - Refresh transactions list

- [ ] 13.6 Implement transaction editing
  - Add edit action to table rows
  - Open form with existing data
  - Submit updated data
  - Refresh transactions list

- [ ] 13.7 Implement transaction deletion
  - Add delete action to table rows
  - Show confirmation dialog
  - Submit delete request (soft delete)
  - Refresh transactions list

- [ ] 13.8 Create FinancialAnalyticsSection component
  - Display profit/loss summary card
  - Display income breakdown pie chart
  - Display expense breakdown pie chart
  - Display financial trends line chart

- [ ] 13.9 Create FinancialBreakdownChart component
  - Implement pie chart using Recharts
  - Display category percentages
  - Add interactive legend
  - Show tooltip with transaction counts

- [ ] 13.10 Create FinancialTrendChart component
  - Implement line chart with three lines: income, expenses, profit
  - Display monthly aggregation
  - Add tooltip with all values
  - Make responsive

- [ ] 13.11 Implement financial analytics data fetching
  - Fetch profit/loss based on date range
  - Fetch income breakdown
  - Fetch expense breakdown
  - Fetch financial trends

- [ ] 13.12 Create useFinancial custom hook
  - Implement data fetching with filters and pagination
  - Handle loading and error states
  - Provide refetch function

## 14. Analytics Page

- [ ] 14.1 Create AnalyticsPage component
  - Implement page layout with header
  - Create sections for different analytics

- [ ] 14.2 Create ProductionComparisonSection component
  - Add two date range selectors for period 1 and period 2
  - Display comparison results (total, average, percentage change)
  - Show side-by-side bar chart

- [ ] 14.3 Create ComparisonChart component
  - Implement bar chart comparing two periods
  - Display total production and average daily for each period
  - Add legend and tooltip

- [ ] 14.4 Implement production comparison data fetching
  - Fetch comparison data from analytics service
  - Calculate percentage changes
  - Handle loading and error states

- [ ] 14.5 Create HerdCompositionSection component
  - Display breed distribution pie chart
  - Show count and percentage for each breed

- [ ] 14.6 Create HealthAnalyticsSection component
  - Display vaccination frequency chart
  - Display treatment frequency chart
  - Show health costs over time

- [ ] 14.7 Create useAnalytics custom hook
  - Implement data fetching for various analytics
  - Handle loading and error states
  - Provide refetch function

## 15. Error Handling and Loading States

- [ ] 15.1 Implement global error handling
  - Set up error boundary at app root
  - Handle uncaught errors gracefully

- [ ] 15.2 Implement API error handling
  - Display user-friendly error messages for API errors
  - Handle 401 (redirect to login)
  - Handle 403 (show permission error)
  - Handle 404 (show not found message)
  - Handle network errors

- [ ] 15.3 Implement loading states for all pages
  - Show loading skeletons while data is fetching
  - Disable buttons during form submission
  - Show loading spinners for actions

- [ ] 15.4 Implement toast notifications
  - Set up Sonner toast provider
  - Show success toasts for successful operations
  - Show error toasts for failed operations
  - Show info toasts for informational messages

## 16. Form Validation

- [ ] 16.1 Implement validation for cow registration form
  - Validate tag ID is not empty
  - Validate date of birth is not in future
  - Validate acquisition date is not before date of birth

- [ ] 16.2 Implement validation for production record form
  - Validate cow is selected
  - Validate date is not in future
  - Validate quantities are non-negative numbers

- [ ] 16.3 Implement validation for health record form
  - Validate cow is selected
  - Validate date is not in future
  - Validate withdrawal period is non-negative integer
  - Validate cost is non-negative number

- [ ] 16.4 Implement validation for transaction form
  - Validate date is not in future
  - Validate amount is positive number
  - Validate type and category are selected

- [ ] 16.5 Implement validation for breeding record form
  - Validate breeding date is not in future
  - Validate expected calving date is after breeding date
  - Validate bull ID is not empty

## 17. Responsive Design

- [ ] 17.1 Implement responsive sidebar
  - Collapse to hamburger menu on mobile
  - Maintain functionality on all screen sizes

- [ ] 17.2 Implement responsive tables
  - Make tables horizontally scrollable on mobile
  - Consider card view for mobile devices

- [ ] 17.3 Implement responsive charts
  - Adjust chart dimensions for mobile screens
  - Simplify chart elements on small screens

- [ ] 17.4 Implement responsive forms
  - Stack form fields vertically on mobile
  - Use full-width inputs on mobile

- [ ] 17.5 Implement responsive metric cards
  - Stack cards vertically on mobile
  - Adjust font sizes for mobile

- [ ] 17.6 Test on multiple devices
  - Test on mobile phones (iOS and Android)
  - Test on tablets
  - Test on desktop browsers

## 18. Performance Optimization

- [ ] 18.1 Implement code splitting
  - Lazy load route components
  - Wrap routes with Suspense

- [ ] 18.2 Implement memoization
  - Memoize expensive computations with useMemo
  - Memoize callbacks with useCallback
  - Memoize components with React.memo

- [ ] 18.3 Implement debouncing
  - Debounce search inputs
  - Debounce filter changes

- [ ] 18.4 Optimize bundle size
  - Configure Vite to split vendor chunks
  - Analyze bundle size with vite-bundle-visualizer

- [ ] 18.5 Implement request deduplication
  - Prevent duplicate API calls
  - Cache frequently accessed data

## 19. Accessibility

- [ ] 19.1 Implement keyboard navigation
  - Ensure all interactive elements are keyboard accessible
  - Implement proper tab order

- [ ] 19.2 Add ARIA labels
  - Add aria-label to icon buttons
  - Add aria-describedby for form errors
  - Add aria-live for dynamic content

- [ ] 19.3 Implement focus management
  - Show visible focus indicators
  - Manage focus for modals and dialogs

- [ ] 19.4 Ensure color contrast
  - Verify all text meets WCAG AA contrast ratios
  - Test with color contrast checker

- [ ] 19.5 Test with screen readers
  - Test with NVDA or JAWS on Windows
  - Test with VoiceOver on macOS

## 20. Testing and Documentation

- [ ] 20.1 Write unit tests for utility functions
  - Test date formatters
  - Test number formatters
  - Test validators

- [ ] 20.2 Write component tests
  - Test MetricCard component
  - Test DataTable component
  - Test form components

- [ ] 20.3 Write integration tests
  - Test login flow
  - Test cow registration flow
  - Test production recording flow

- [ ] 20.4 Create README.md
  - Document project setup
  - Document available scripts
  - Document environment variables
  - Document project structure

- [ ] 20.5 Create API documentation
  - Document service layer functions
  - Document custom hooks
  - Document component props

## 21. Deployment Preparation

- [ ] 21.1 Configure production build
  - Set up Vite production configuration
  - Configure environment variables for production
  - Test production build locally

- [ ] 21.2 Optimize assets
  - Compress images
  - Minify CSS and JavaScript
  - Enable gzip compression

- [ ] 21.3 Set up CI/CD pipeline
  - Configure build pipeline
  - Configure deployment pipeline
  - Set up automated testing

- [ ] 21.4 Create deployment documentation
  - Document deployment process
  - Document environment setup
  - Document troubleshooting steps
