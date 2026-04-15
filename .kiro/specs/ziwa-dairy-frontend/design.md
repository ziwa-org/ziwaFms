# Design Document: Ziwa Dairy Farm Management System Frontend

## Overview

The Ziwa Dairy Farm Management System Frontend is a modern, responsive React application built with TypeScript and Vite. The application provides a comprehensive user interface for managing all aspects of dairy farm operations, including livestock tracking, milk production recording, health management, financial tracking, and analytics.

The frontend architecture follows React best practices with a component-based design, centralized state management, and clean separation of concerns. The UI adapts the proven design patterns from the Energy Management System Dashboard, maintaining the same visual language, component library (shadcn/ui), and charting library (Recharts) while tailoring the functionality for dairy farm management.

### Key Design Principles

1. **Consistency**: Maintain visual and interaction consistency across all pages by reusing components and patterns
2. **Responsiveness**: Ensure the application works seamlessly across desktop, tablet, and mobile devices
3. **Performance**: Optimize for fast load times and smooth interactions through code splitting, lazy loading, and efficient state management
4. **Accessibility**: Follow WCAG guidelines to ensure the application is usable by everyone
5. **Security**: Implement secure authentication, authorization, and data handling practices
6. **Maintainability**: Write clean, well-documented code with clear component boundaries

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Browser Application                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              Presentation Layer                        │  │
│  │  - React Components (Pages, Layouts, UI Components)   │  │
│  │  - shadcn/ui Component Library                         │  │
│  │  - Recharts Visualizations                             │  │
│  └───────────────────────────────────────────────────────┘  │
│                          │                                    │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              Application Layer                         │  │
│  │  - React Router (Navigation)                           │  │
│  │  - React Context (State Management)                    │  │
│  │  - Custom Hooks (Business Logic)                       │  │
│  └───────────────────────────────────────────────────────┘  │
│                          │                                    │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              Data Access Layer                         │  │
│  │  - API Client (Axios/Fetch)                            │  │
│  │  - Request/Response Interceptors                       │  │
│  │  - Data Transformation                                 │  │
│  └───────────────────────────────────────────────────────┘  │
│                          │                                    │
└──────────────────────────┼────────────────────────────────────┘
                           │
                           │ HTTPS + JWT
                           │
┌──────────────────────────▼────────────────────────────────────┐
│              Spring Boot Backend API                          │
│  - Authentication & Authorization                             │
│  - Business Logic                                             │
│  - Data Persistence                                           │
└───────────────────────────────────────────────────────────────┘
```

### Technology Stack

- **Build Tool**: Vite 6.3.5 (fast development server, optimized production builds)
- **Framework**: React 18.3.1 with TypeScript
- **Routing**: React Router v6 (client-side routing with protected routes)
- **Styling**: Tailwind CSS 4.1.12 (utility-first CSS framework)
- **UI Components**: shadcn/ui (Radix UI primitives with Tailwind styling)
- **Charts**: Recharts 2.15.2 (declarative charting library)
- **Icons**: Lucide React 0.487.0 (consistent icon set)
- **Forms**: React Hook Form 7.55.0 (performant form validation)
- **HTTP Client**: Axios (promise-based HTTP client with interceptors)
- **State Management**: React Context API + Custom Hooks
- **Date Handling**: date-fns (lightweight date utility library)
- **Notifications**: Sonner 2.0.3 (toast notifications)

### Project Structure

```
frontend/
├── public/                      # Static assets
├── src/
│   ├── components/              # Reusable UI components
│   │   ├── ui/                  # shadcn/ui base components
│   │   ├── layout/              # Layout components
│   │   │   ├── Sidebar.tsx
│   │   │   ├── Header.tsx
│   │   │   └── Layout.tsx
│   │   ├── charts/              # Chart components
│   │   │   ├── ProductionTrendChart.tsx
│   │   │   ├── FinancialBreakdownChart.tsx
│   │   │   └── ComparisonChart.tsx
│   │   ├── forms/               # Form components
│   │   │   ├── CowRegistrationForm.tsx
│   │   │   ├── ProductionRecordForm.tsx
│   │   │   ├── HealthRecordForm.tsx
│   │   │   └── TransactionForm.tsx
│   │   └── common/              # Common components
│   │       ├── MetricCard.tsx
│   │       ├── DataTable.tsx
│   │       ├── FilterSection.tsx
│   │       └── LoadingSkeleton.tsx
│   ├── pages/                   # Page components
│   │   ├── auth/
│   │   │   ├── LoginPage.tsx
│   │   │   └── RegisterPage.tsx
│   │   ├── DashboardPage.tsx
│   │   ├── livestock/
│   │   │   ├── LivestockListPage.tsx
│   │   │   └── CowDetailsPage.tsx
│   │   ├── production/
│   │   │   └── ProductionPage.tsx
│   │   ├── health/
│   │   │   └── HealthPage.tsx
│   │   ├── financial/
│   │   │   └── FinancialPage.tsx
│   │   └── analytics/
│   │       └── AnalyticsPage.tsx
│   ├── hooks/                   # Custom React hooks
│   │   ├── useAuth.ts
│   │   ├── useCows.ts
│   │   ├── useProduction.ts
│   │   ├── useHealth.ts
│   │   ├── useFinancial.ts
│   │   └── useAnalytics.ts
│   ├── contexts/                # React contexts
│   │   └── AuthContext.tsx
│   ├── services/                # API service layer
│   │   ├── api.ts               # Axios instance configuration
│   │   ├── authService.ts
│   │   ├── cowService.ts
│   │   ├── productionService.ts
│   │   ├── healthService.ts
│   │   ├── financialService.ts
│   │   └── analyticsService.ts
│   ├── types/                   # TypeScript type definitions
│   │   ├── auth.types.ts
│   │   ├── cow.types.ts
│   │   ├── production.types.ts
│   │   ├── health.types.ts
│   │   ├── financial.types.ts
│   │   └── common.types.ts
│   ├── utils/                   # Utility functions
│   │   ├── formatters.ts        # Date, number, currency formatters
│   │   ├── validators.ts        # Form validation helpers
│   │   └── constants.ts         # Application constants
│   ├── routes/                  # Route configuration
│   │   ├── AppRoutes.tsx
│   │   └── ProtectedRoute.tsx
│   ├── App.tsx                  # Root component
│   ├── main.tsx                 # Application entry point
│   └── index.css                # Global styles
├── .env.example                 # Environment variables template
├── vite.config.ts               # Vite configuration
├── tailwind.config.js           # Tailwind configuration
├── tsconfig.json                # TypeScript configuration
└── package.json                 # Dependencies and scripts
```

## Components and Interfaces

### Core Components

#### 1. Layout Components

**Sidebar Component**
- Collapsible navigation sidebar with gradient styling
- Navigation items: Dashboard, Livestock, Production, Health, Financial, Analytics
- Active state highlighting with animations
- User profile section at bottom
- Responsive: Collapses to hamburger menu on mobile

```typescript
interface SidebarProps {
  currentPage: string;
  onPageChange: (page: string) => void;
  isExpanded: boolean;
  onToggleExpand: () => void;
}

interface NavigationItem {
  id: string;
  name: string;
  icon: LucideIcon;
  description: string;
  path: string;
}
```

**Header Component**
- User greeting with full name
- Role badge (Admin, Manager, Viewer)
- Real-time status indicator
- Logout button
- Responsive layout

```typescript
interface HeaderProps {
  user: User;
  onLogout: () => void;
}
```

**Layout Component**
- Main application layout wrapper
- Combines Sidebar and Header
- Provides content area with proper spacing
- Handles responsive breakpoints

#### 2. Dashboard Components

**MetricCard Component**
- Displays key performance indicators
- Icon, title, value, and optional trend indicator
- Gradient background with hover effects
- Loading skeleton state

```typescript
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
```

**ProductionTrendChart Component**
- Line chart showing 30-day production trends
- X-axis: dates, Y-axis: total production in liters
- Tooltip showing date and production value
- Responsive sizing

```typescript
interface ProductionTrendChartProps {
  data: Array<{
    date: string;
    totalProduction: number;
  }>;
  isLoading?: boolean;
}
```

**TopProducersTable Component**
- Table displaying top 5 producing cows
- Columns: Rank, Tag ID, Total Production, Average Daily
- Click to navigate to cow details

```typescript
interface TopProducersTableProps {
  producers: Array<{
    cowId: number;
    cowTagId: string;
    totalProduction: number;
    averageProduction: number;
  }>;
  isLoading?: boolean;
}
```

#### 3. Livestock Components

**DataTable Component**
- Generic reusable table with pagination
- Column configuration with sorting
- Row selection and actions
- Filter integration
- Loading and empty states

```typescript
interface DataTableProps<T> {
  columns: ColumnDef<T>[];
  data: T[];
  pagination: PaginationState;
  onPaginationChange: (pagination: PaginationState) => void;
  isLoading?: boolean;
  onRowClick?: (row: T) => void;
}

interface ColumnDef<T> {
  id: string;
  header: string;
  accessorKey: keyof T;
  cell?: (value: any) => React.ReactNode;
  sortable?: boolean;
}
```

**FilterSection Component**
- Reusable filter controls
- Dropdown filters, date range pickers
- Apply and clear buttons
- Responsive layout

```typescript
interface FilterSectionProps {
  filters: FilterConfig[];
  values: Record<string, any>;
  onChange: (key: string, value: any) => void;
  onApply: () => void;
  onClear: () => void;
}

interface FilterConfig {
  key: string;
  label: string;
  type: 'select' | 'dateRange' | 'text';
  options?: Array<{ label: string; value: string }>;
}
```

**CowRegistrationForm Component**
- Form for registering new cows
- Fields: Tag ID, Breed, Date of Birth, Acquisition Date, Status
- Validation with React Hook Form
- Submit and cancel actions

```typescript
interface CowRegistrationFormProps {
  onSubmit: (data: CowRegistrationData) => Promise<void>;
  onCancel: () => void;
  initialData?: Partial<CowRegistrationData>;
  isEdit?: boolean;
}

interface CowRegistrationData {
  tagId: string;
  breed: string;
  dateOfBirth: string;
  acquisitionDate: string;
  status: CowStatus;
}
```

#### 4. Production Components

**ProductionRecordForm Component**
- Form for recording milk production
- Cow selector, date picker, morning/evening quantities
- Auto-calculation of total quantity
- Validation and error handling

```typescript
interface ProductionRecordFormProps {
  cows: Cow[];
  onSubmit: (data: ProductionRecordData) => Promise<void>;
  onCancel: () => void;
  initialData?: Partial<ProductionRecordData>;
}

interface ProductionRecordData {
  cowId: number;
  date: string;
  morningQuantity: number;
  eveningQuantity: number;
  notes?: string;
}
```

#### 5. Health Components

**HealthRecordForm Component**
- Form for creating health records
- Record type selector, medication details, withdrawal period
- Cost tracking
- Veterinarian information

```typescript
interface HealthRecordFormProps {
  cows: Cow[];
  onSubmit: (data: HealthRecordData) => Promise<void>;
  onCancel: () => void;
}

interface HealthRecordData {
  cowId: number;
  date: string;
  recordType: HealthRecordType;
  description: string;
  veterinarianName?: string;
  medication?: string;
  withdrawalPeriodDays?: number;
  cost?: number;
}
```

**WithdrawalPeriodCard Component**
- Displays cows in withdrawal period
- Shows days remaining with visual indicator
- Color-coded urgency (red for < 3 days)

```typescript
interface WithdrawalPeriodCardProps {
  withdrawals: Array<{
    cowId: number;
    cowTagId: string;
    withdrawalEndDate: string;
    daysRemaining: number;
    medication: string;
  }>;
}
```

#### 6. Financial Components

**TransactionForm Component**
- Form for recording transactions
- Type selector (Income/Expense)
- Category dropdown (dynamic based on type)
- Amount, date, description, reference ID

```typescript
interface TransactionFormProps {
  onSubmit: (data: TransactionData) => Promise<void>;
  onCancel: () => void;
}

interface TransactionData {
  date: string;
  type: TransactionType;
  category: string;
  amount: number;
  description: string;
  referenceId?: string;
}
```

**FinancialBreakdownChart Component**
- Pie chart for income/expense breakdown
- Shows category percentages
- Interactive legend
- Tooltip with transaction counts

```typescript
interface FinancialBreakdownChartProps {
  data: Array<{
    category: string;
    total: number;
    percentage: number;
    transactionCount: number;
  }>;
  type: 'income' | 'expense';
}
```

**FinancialTrendChart Component**
- Line chart with three lines: income, expenses, profit
- Monthly aggregation
- Date range filtering
- Responsive design

```typescript
interface FinancialTrendChartProps {
  data: Array<{
    month: string;
    totalIncome: number;
    totalExpenses: number;
    netProfit: number;
  }>;
}
```

### Service Layer Interfaces

#### API Client Configuration

```typescript
// Base Axios instance with interceptors
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid - redirect to login
      localStorage.removeItem('jwt_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

#### Authentication Service

```typescript
interface AuthService {
  login(credentials: LoginCredentials): Promise<AuthResponse>;
  register(data: RegisterData): Promise<AuthResponse>;
  logout(): void;
  refreshToken(): Promise<AuthResponse>;
  getCurrentUser(): Promise<User>;
}

interface LoginCredentials {
  username: string;
  password: string;
}

interface RegisterData {
  username: string;
  password: string;
  fullName: string;
  role: UserRole;
}

interface AuthResponse {
  token: string;
  type: string;
  expiresIn: number;
  user: User;
}

interface User {
  id: number;
  username: string;
  fullName: string;
  role: UserRole;
}

type UserRole = 'ADMIN' | 'MANAGER' | 'USER';
```

#### Cow Service

```typescript
interface CowService {
  getCows(params: CowListParams): Promise<PagedResponse<Cow>>;
  getCowById(id: number): Promise<Cow>;
  createCow(data: CowRegistrationData): Promise<Cow>;
  updateCow(id: number, data: CowRegistrationData): Promise<Cow>;
  deleteCow(id: number): Promise<void>;
  updateCowStatus(id: number, status: CowStatus): Promise<Cow>;
  getBreedingRecords(cowId: number): Promise<BreedingRecord[]>;
  addBreedingRecord(cowId: number, data: BreedingRecordData): Promise<BreedingRecord>;
}

interface CowListParams {
  status?: CowStatus;
  breed?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

interface Cow {
  id: number;
  tagId: string;
  breed: string;
  dateOfBirth: string;
  acquisitionDate: string;
  status: CowStatus;
  createdAt: string;
  updatedAt: string;
}

type CowStatus = 'ACTIVE' | 'SOLD' | 'DECEASED';

interface BreedingRecord {
  id: number;
  cowId: number;
  cowTagId: string;
  breedingDate: string;
  bullId: string;
  expectedCalvingDate: string;
  actualCalvingDate?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}
```

#### Production Service

```typescript
interface ProductionService {
  getProductionRecords(params: ProductionListParams): Promise<PagedResponse<ProductionRecord>>;
  getProductionById(id: number): Promise<ProductionRecord>;
  createProductionRecord(data: ProductionRecordData): Promise<ProductionRecord>;
  updateProductionRecord(id: number, data: ProductionRecordData): Promise<ProductionRecord>;
  deleteProductionRecord(id: number): Promise<void>;
  getProductionTrends(startDate: string, endDate: string): Promise<ProductionTrend[]>;
  getCowProductivity(limit?: number): Promise<CowProductivity[]>;
  getTopProducers(limit?: number): Promise<TopProducer[]>;
}

interface ProductionListParams {
  cowId?: number;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

interface ProductionRecord {
  id: number;
  cowId: number;
  cowTagId: string;
  date: string;
  morningQuantity: number;
  eveningQuantity: number;
  totalQuantity: number;
  notes?: string;
  createdAt: string;
}

interface ProductionTrend {
  date: string;
  totalProduction: number;
  averagePerCow: number;
  recordCount: number;
}

interface TopProducer {
  cowId: number;
  cowTagId: string;
  totalProduction: number;
  averageProduction: number;
  recordCount: number;
}
```

#### Health Service

```typescript
interface HealthService {
  getHealthRecords(params: HealthListParams): Promise<PagedResponse<HealthRecord>>;
  getHealthRecordById(id: number): Promise<HealthRecord>;
  createHealthRecord(data: HealthRecordData): Promise<HealthRecord>;
  updateHealthRecord(id: number, data: HealthRecordData): Promise<HealthRecord>;
  deleteHealthRecord(id: number): Promise<void>;
  getActiveWithdrawals(): Promise<WithdrawalInfo[]>;
}

interface HealthListParams {
  cowId?: number;
  recordType?: HealthRecordType;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

interface HealthRecord {
  id: number;
  cowId: number;
  cowTagId: string;
  date: string;
  recordType: HealthRecordType;
  description: string;
  veterinarianName?: string;
  medication?: string;
  withdrawalPeriodDays?: number;
  withdrawalEndDate?: string;
  cost?: number;
  createdAt: string;
}

type HealthRecordType = 'VACCINATION' | 'TREATMENT' | 'CHECKUP';

interface WithdrawalInfo {
  cowId: number;
  cowTagId: string;
  healthRecordId: number;
  withdrawalEndDate: string;
  daysRemaining: number;
  medication: string;
}
```

#### Financial Service

```typescript
interface FinancialService {
  getTransactions(params: TransactionListParams): Promise<PagedResponse<Transaction>>;
  getTransactionById(id: number): Promise<Transaction>;
  createTransaction(data: TransactionData): Promise<Transaction>;
  updateTransaction(id: number, data: TransactionData): Promise<Transaction>;
  deleteTransaction(id: number): Promise<void>;
  getProfitLoss(startDate: string, endDate: string): Promise<ProfitLossAnalysis>;
  getIncomeBreakdown(startDate: string, endDate: string): Promise<CategoryBreakdown[]>;
  getExpenseBreakdown(startDate: string, endDate: string): Promise<CategoryBreakdown[]>;
  getFinancialTrends(startDate: string, endDate: string): Promise<MonthlyTrend[]>;
}

interface TransactionListParams {
  type?: TransactionType;
  category?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

interface Transaction {
  id: number;
  date: string;
  type: TransactionType;
  category: string;
  amount: number;
  description: string;
  referenceId?: string;
  deleted: boolean;
  createdAt: string;
  updatedAt: string;
}

type TransactionType = 'INCOME' | 'EXPENSE';

interface ProfitLossAnalysis {
  startDate: string;
  endDate: string;
  totalIncome: number;
  totalExpenses: number;
  netProfit: number;
  profitMargin: number;
}

interface CategoryBreakdown {
  category: string;
  total: number;
  percentage: number;
  transactionCount: number;
}

interface MonthlyTrend {
  month: string;
  totalIncome: number;
  totalExpenses: number;
  netProfit: number;
}
```

#### Analytics Service

```typescript
interface AnalyticsService {
  getDashboardSummary(): Promise<DashboardSummary>;
  getProductionComparison(params: ComparisonParams): Promise<ProductionComparison>;
}

interface DashboardSummary {
  activeCowsCount: number;
  todayProduction: number;
  monthlyFinancialSummary: MonthlyTrend;
  cowsInWithdrawal: number;
  upcomingVaccinations: UpcomingVaccination[];
  productionTrend30Days: ProductionTrend[];
  topProducers: TopProducer[];
}

interface UpcomingVaccination {
  cowId: number;
  cowTagId: string;
  vaccinationDate: string;
  description: string;
}

interface ComparisonParams {
  startDate1: string;
  endDate1: string;
  startDate2: string;
  endDate2: string;
}

interface ProductionComparison {
  period1: PeriodStats;
  period2: PeriodStats;
  comparison: ComparisonStats;
}

interface PeriodStats {
  startDate: string;
  endDate: string;
  totalProduction: number;
  averageDaily: number;
  recordCount: number;
}

interface ComparisonStats {
  productionChange: number;
  productionChangePercentage: number;
  averageDailyChange: number;
  averageDailyChangePercentage: number;
}
```

### Common Types

```typescript
interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

interface PaginationState {
  pageIndex: number;
  pageSize: number;
}

interface SortState {
  sortBy: string;
  sortDirection: 'asc' | 'desc';
}

interface ApiError {
  code: string;
  message: string;
  errors?: FieldError[];
  timestamp: string;
}

interface FieldError {
  field: string;
  message: string;
}
```

## Data Models

### Frontend Data Models

The frontend uses TypeScript interfaces to define data models that mirror the backend API responses. These models provide type safety and enable better IDE support.

#### Authentication Models

```typescript
interface LoginFormData {
  username: string;
  password: string;
}

interface RegisterFormData {
  username: string;
  password: string;
  confirmPassword: string;
  fullName: string;
  role: UserRole;
}

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}
```

#### Livestock Models

```typescript
interface CowFormData {
  tagId: string;
  breed: string;
  dateOfBirth: Date;
  acquisitionDate: Date;
  status: CowStatus;
}

interface BreedingFormData {
  breedingDate: Date;
  bullId: string;
  expectedCalvingDate: Date;
  notes: string;
}

interface LivestockState {
  cows: Cow[];
  selectedCow: Cow | null;
  filters: CowListParams;
  pagination: PaginationState;
  isLoading: boolean;
  error: string | null;
}
```

#### Production Models

```typescript
interface ProductionFormData {
  cowId: number;
  date: Date;
  morningQuantity: number;
  eveningQuantity: number;
  notes: string;
}

interface ProductionState {
  records: ProductionRecord[];
  trends: ProductionTrend[];
  topProducers: TopProducer[];
  filters: ProductionListParams;
  pagination: PaginationState;
  isLoading: boolean;
  error: string | null;
}
```

#### Health Models

```typescript
interface HealthFormData {
  cowId: number;
  date: Date;
  recordType: HealthRecordType;
  description: string;
  veterinarianName: string;
  medication: string;
  withdrawalPeriodDays: number;
  cost: number;
}

interface HealthState {
  records: HealthRecord[];
  activeWithdrawals: WithdrawalInfo[];
  filters: HealthListParams;
  pagination: PaginationState;
  isLoading: boolean;
  error: string | null;
}
```

#### Financial Models

```typescript
interface TransactionFormData {
  date: Date;
  type: TransactionType;
  category: string;
  amount: number;
  description: string;
  referenceId: string;
}

interface FinancialState {
  transactions: Transaction[];
  profitLoss: ProfitLossAnalysis | null;
  incomeBreakdown: CategoryBreakdown[];
  expenseBreakdown: CategoryBreakdown[];
  trends: MonthlyTrend[];
  filters: TransactionListParams;
  dateRange: { startDate: string; endDate: string };
  pagination: PaginationState;
  isLoading: boolean;
  error: string | null;
}
```

### Data Transformation

The frontend implements data transformation utilities to convert between API responses and UI-friendly formats:

```typescript
// Date formatting
function formatDate(date: string | Date, format: string): string;
function parseDate(dateString: string): Date;

// Number formatting
function formatCurrency(amount: number): string;
function formatNumber(value: number, decimals: number): string;
function formatPercentage(value: number): string;

// Data transformation
function transformCowForDisplay(cow: Cow): CowDisplayData;
function transformProductionForChart(records: ProductionRecord[]): ChartData[];
function transformFinancialForChart(trends: MonthlyTrend[]): ChartData[];
```

## 

## State Management

### Authentication Context

The application uses React Context for global authentication state management:

```typescript
interface AuthContextValue {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginCredentials) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AuthState>({
    user: null,
    token: localStorage.getItem('jwt_token'),
    isAuthenticated: false,
    isLoading: true,
    error: null,
  });

  // Implementation details...
  
  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
```

### Custom Hooks for Data Fetching

Each domain uses custom hooks for data fetching and state management:

```typescript
// useCows hook
export function useCows(params?: CowListParams) {
  const [state, setState] = useState<LivestockState>({
    cows: [],
    selectedCow: null,
    filters: params || {},
    pagination: { pageIndex: 0, pageSize: 20 },
    isLoading: false,
    error: null,
  });

  const fetchCows = useCallback(async () => {
    setState(prev => ({ ...prev, isLoading: true, error: null }));
    try {
      const response = await cowService.getCows({
        ...state.filters,
        page: state.pagination.pageIndex,
        size: state.pagination.pageSize,
      });
      setState(prev => ({ ...prev, cows: response.content, isLoading: false }));
    } catch (error) {
      setState(prev => ({ ...prev, error: error.message, isLoading: false }));
    }
  }, [state.filters, state.pagination]);

  useEffect(() => {
    fetchCows();
  }, [fetchCows]);

  return {
    ...state,
    refetch: fetchCows,
    setFilters: (filters: CowListParams) => setState(prev => ({ ...prev, filters })),
    setPagination: (pagination: PaginationState) => setState(prev => ({ ...prev, pagination })),
  };
}
```

## Routing

### Route Configuration

```typescript
// AppRoutes.tsx
export function AppRoutes() {
  return (
    <Routes>
      {/* Public routes */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      
      {/* Protected routes */}
      <Route element={<ProtectedRoute />}>
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<DashboardPage />} />
          <Route path="livestock">
            <Route index element={<LivestockListPage />} />
            <Route path=":id" element={<CowDetailsPage />} />
          </Route>
          <Route path="production" element={<ProductionPage />} />
          <Route path="health" element={<HealthPage />} />
          <Route path="financial" element={<FinancialPage />} />
          <Route path="analytics" element={<AnalyticsPage />} />
        </Route>
      </Route>
      
      {/* 404 */}
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}

// ProtectedRoute.tsx
export function ProtectedRoute() {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return <LoadingScreen />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
}
```

## Styling and Theming

### Tailwind Configuration

```javascript
// tailwind.config.js
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        // Sidebar colors (matching Energy Management Dashboard)
        sidebar: {
          DEFAULT: 'hsl(210, 40%, 15%)',
          accent: 'hsl(210, 35%, 20%)',
          border: 'hsl(210, 30%, 25%)',
          foreground: 'hsl(210, 20%, 95%)',
          primary: 'hsl(142, 76%, 36%)',
          'primary-foreground': 'hsl(0, 0%, 100%)',
          'accent-foreground': 'hsl(210, 20%, 80%)',
        },
        // Main content colors
        background: 'hsl(0, 0%, 100%)',
        foreground: 'hsl(222.2, 84%, 4.9%)',
        primary: {
          DEFAULT: 'hsl(142, 76%, 36%)',
          foreground: 'hsl(0, 0%, 100%)',
        },
        secondary: {
          DEFAULT: 'hsl(210, 40%, 96.1%)',
          foreground: 'hsl(222.2, 47.4%, 11.2%)',
        },
        muted: {
          DEFAULT: 'hsl(210, 40%, 96.1%)',
          foreground: 'hsl(215.4, 16.3%, 46.9%)',
        },
        accent: {
          DEFAULT: 'hsl(210, 40%, 96.1%)',
          foreground: 'hsl(222.2, 47.4%, 11.2%)',
        },
        destructive: {
          DEFAULT: 'hsl(0, 84.2%, 60.2%)',
          foreground: 'hsl(0, 0%, 100%)',
        },
        border: 'hsl(214.3, 31.8%, 91.4%)',
        input: 'hsl(214.3, 31.8%, 91.4%)',
        ring: 'hsl(142, 76%, 36%)',
      },
      borderRadius: {
        lg: '0.5rem',
        md: '0.375rem',
        sm: '0.25rem',
      },
    },
  },
  plugins: [],
};
```

### Global Styles

```css
/* index.css */
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  * {
    @apply border-border;
  }
  
  body {
    @apply bg-background text-foreground;
    font-feature-settings: "rlig" 1, "calt" 1;
  }
}

@layer utilities {
  .scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
  }
  
  .scrollbar-hide::-webkit-scrollbar {
    display: none;
  }
}
```

## Error Handling

### Error Boundary Component

```typescript
interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends React.Component<
  { children: React.ReactNode },
  ErrorBoundaryState
> {
  constructor(props: { children: React.ReactNode }) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex items-center justify-center min-h-screen">
          <div className="text-center">
            <h1 className="text-2xl font-bold mb-4">Something went wrong</h1>
            <p className="text-muted-foreground mb-4">
              {this.state.error?.message}
            </p>
            <button
              onClick={() => window.location.reload()}
              className="px-4 py-2 bg-primary text-primary-foreground rounded-md"
            >
              Reload Page
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
```

### API Error Handling

```typescript
export function handleApiError(error: any): string {
  if (axios.isAxiosError(error)) {
    if (error.response) {
      const apiError = error.response.data as ApiError;
      if (apiError.errors && apiError.errors.length > 0) {
        return apiError.errors.map(e => e.message).join(', ');
      }
      return apiError.message || 'An error occurred';
    }
    if (error.request) {
      return 'Network error. Please check your connection.';
    }
  }
  return error.message || 'An unexpected error occurred';
}
```

## Performance Optimization

### Code Splitting

```typescript
// Lazy load route components
const DashboardPage = lazy(() => import('./pages/DashboardPage'));
const LivestockListPage = lazy(() => import('./pages/livestock/LivestockListPage'));
const ProductionPage = lazy(() => import('./pages/production/ProductionPage'));
const HealthPage = lazy(() => import('./pages/health/HealthPage'));
const FinancialPage = lazy(() => import('./pages/financial/FinancialPage'));
const AnalyticsPage = lazy(() => import('./pages/analytics/AnalyticsPage'));

// Wrap with Suspense
<Suspense fallback={<LoadingScreen />}>
  <Routes>
    {/* routes */}
  </Routes>
</Suspense>
```

### Memoization

```typescript
// Memoize expensive computations
const chartData = useMemo(() => {
  return transformProductionForChart(productionRecords);
}, [productionRecords]);

// Memoize callbacks
const handleFilterChange = useCallback((key: string, value: any) => {
  setFilters(prev => ({ ...prev, [key]: value }));
}, []);

// Memoize components
const MetricCard = memo(({ title, value, icon }: MetricCardProps) => {
  // component implementation
});
```

### Debouncing

```typescript
// Debounce search inputs
const debouncedSearch = useMemo(
  () => debounce((value: string) => {
    setFilters(prev => ({ ...prev, search: value }));
  }, 300),
  []
);
```

## Testing Strategy

### Unit Tests

- Test utility functions (formatters, validators)
- Test custom hooks with React Testing Library
- Test service layer functions with mocked API responses

### Component Tests

- Test component rendering with different props
- Test user interactions (clicks, form submissions)
- Test conditional rendering based on state
- Test accessibility features

### Integration Tests

- Test complete user flows (login, create cow, record production)
- Test navigation between pages
- Test form validation and submission
- Test error handling

### E2E Tests (Future)

- Test critical user journeys
- Test across different browsers
- Test responsive behavior

## Deployment

### Build Configuration

```typescript
// vite.config.ts
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: 'dist',
    sourcemap: false,
    minify: 'terser',
    rollupOptions: {
      output: {
        manualChunks: {
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'ui-vendor': ['@radix-ui/react-dialog', '@radix-ui/react-dropdown-menu'],
          'chart-vendor': ['recharts'],
        },
      },
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

### Environment Variables

```bash
# .env.example
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_NAME=Ziwa Dairy Farm Management
VITE_APP_VERSION=1.0.0
```

### Production Build

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

## Security Considerations

1. **JWT Token Storage**: Store JWT in localStorage with expiration handling
2. **XSS Prevention**: Sanitize user inputs, use React's built-in XSS protection
3. **CSRF Protection**: Include CSRF tokens for state-changing operations
4. **HTTPS**: Enforce HTTPS in production
5. **Content Security Policy**: Implement CSP headers
6. **Input Validation**: Validate all user inputs on the frontend and backend
7. **Secure Dependencies**: Regularly update dependencies and scan for vulnerabilities

## Accessibility

1. **Keyboard Navigation**: All interactive elements accessible via keyboard
2. **Screen Reader Support**: Proper ARIA labels and semantic HTML
3. **Focus Management**: Visible focus indicators and logical focus order
4. **Color Contrast**: WCAG AA compliant color contrast ratios
5. **Form Labels**: All form inputs have associated labels
6. **Error Announcements**: Error messages announced to screen readers
7. **Responsive Text**: Text scales appropriately with browser zoom

## Browser Support

- Chrome (last 2 versions)
- Firefox (last 2 versions)
- Safari (last 2 versions)
- Edge (last 2 versions)
- Mobile browsers (iOS Safari, Chrome Android)

## Future Enhancements

1. **Offline Support**: Implement service workers for offline functionality
2. **Real-time Updates**: WebSocket integration for live data updates
3. **Mobile App**: React Native version for iOS and Android
4. **Advanced Analytics**: Machine learning predictions for production trends
5. **Export Functionality**: PDF and Excel export for reports
6. **Multi-language Support**: Internationalization (i18n)
7. **Dark Mode**: Theme switching capability
8. **Notifications**: Push notifications for important events
9. **Data Visualization**: More advanced charts and dashboards
10. **Integration**: Third-party integrations (accounting software, IoT devices)
