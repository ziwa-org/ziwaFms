# Design Document: Ziwa Dairy Farm Analytics and Management System REST API

## Overview

The Ziwa Dairy Farm Analytics and Management System is a production-ready REST API built with Spring Boot and PostgreSQL. The system provides comprehensive dairy farm management through five core modules: Livestock Management, Milk Production Tracking, Health Management, Financial Management, and Analytics. The API follows RESTful principles, implements JWT-based authentication, and provides comprehensive documentation for frontend integration.

The design leverages Spring Boot's ecosystem including Spring Data JPA for data persistence, Spring Security for authentication/authorization, and Spring Validation for input validation. The system is designed to be stateless, scalable, and maintainable with clear separation of concerns across controller, service, and repository layers.

## Architecture

### System Architecture

The system follows a layered architecture pattern:

```
┌─────────────────────────────────────────┐
│         API Clients (Frontend)          │
└─────────────────┬───────────────────────┘
                  │ HTTPS/JSON
┌─────────────────▼───────────────────────┐
│         Controller Layer                │
│  - Request validation                   │
│  - Response formatting                  │
│  - Exception handling                   │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Service Layer                   │
│  - Business logic                       │
│  - Transaction management               │
│  - Data transformation                  │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Repository Layer                │
│  - Data access                          │
│  - Query execution                      │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         PostgreSQL Database             │
└─────────────────────────────────────────┘
```

### Security Architecture

Authentication and authorization flow:

```
1. User Login → JWT Token Generation
2. API Request with JWT Token → Token Validation
3. Extract User Info → Check Permissions
4. Execute Request → Return Response
```

JWT tokens contain:
- User ID
- Username
- Role
- Expiration timestamp

### Module Organization

The system is organized into five core modules:

1. **Livestock Module**: Cow registration, lifecycle management, breeding records
2. **Production Module**: Milk production recording and analytics
3. **Health Module**: Health records, treatments, withdrawal tracking
4. **Financial Module**: Transaction management and financial analytics
5. **Analytics Module**: Cross-module reporting and dashboard data

## Components and Interfaces

### Controller Layer

#### CowController
Handles livestock management endpoints.

**Endpoints:**
- `POST /api/cows` - Register new cow
- `GET /api/cows/{id}` - Get cow by ID
- `GET /api/cows` - List cows with filtering (status, breed) and pagination
- `PUT /api/cows/{id}` - Update cow information
- `DELETE /api/cows/{id}` - Delete cow (with validation)
- `PATCH /api/cows/{id}/status` - Update cow status
- `POST /api/cows/{id}/breeding` - Add breeding record
- `GET /api/cows/{id}/breeding` - Get breeding records for cow

**Request/Response Models:**
```java
CowRegistrationRequest {
  tagId: String (required, unique)
  breed: String (required)
  dateOfBirth: LocalDate (required)
  acquisitionDate: LocalDate (required)
  status: CowStatus (default: ACTIVE)
}

CowResponse {
  id: Long
  tagId: String
  breed: String
  dateOfBirth: LocalDate
  acquisitionDate: LocalDate
  status: CowStatus
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}

BreedingRecordRequest {
  breedingDate: LocalDate (required, not future)
  bullId: String
  expectedCalvingDate: LocalDate
  notes: String
}
```

#### MilkProductionController
Handles milk production recording and analytics.

**Endpoints:**
- `POST /api/production` - Record milk production
- `GET /api/production` - List production records (filter by cow, date range)
- `GET /api/production/{id}` - Get production record by ID
- `PUT /api/production/{id}` - Update production record
- `DELETE /api/production/{id}` - Delete production record
- `GET /api/production/analytics/trends` - Get production trends
- `GET /api/production/analytics/cow-productivity` - Get per-cow productivity
- `GET /api/production/analytics/top-producers` - Get top producing cows

**Request/Response Models:**
```java
ProductionRecordRequest {
  cowId: Long (required)
  date: LocalDate (required)
  morningQuantity: Double (required, >= 0)
  eveningQuantity: Double (required, >= 0)
  notes: String
}

ProductionRecordResponse {
  id: Long
  cowId: Long
  cowTagId: String
  date: LocalDate
  morningQuantity: Double
  eveningQuantity: Double
  totalQuantity: Double
  notes: String
  createdAt: LocalDateTime
}

ProductionTrendResponse {
  date: LocalDate
  totalProduction: Double
  averagePerCow: Double
  recordCount: Integer
}
```

#### HealthController
Handles health records and withdrawal tracking.

**Endpoints:**
- `POST /api/health` - Create health record
- `GET /api/health` - List health records (filter by cow, type, date range)
- `GET /api/health/{id}` - Get health record by ID
- `PUT /api/health/{id}` - Update health record
- `DELETE /api/health/{id}` - Delete health record
- `GET /api/health/withdrawals/active` - Get cows in withdrawal period

**Request/Response Models:**
```java
HealthRecordRequest {
  cowId: Long (required)
  date: LocalDate (required)
  recordType: HealthRecordType (required) // VACCINATION, TREATMENT, CHECKUP
  description: String (required)
  veterinarianName: String
  medication: String
  withdrawalPeriodDays: Integer (>= 0)
  cost: Double (>= 0)
}

HealthRecordResponse {
  id: Long
  cowId: Long
  cowTagId: String
  date: LocalDate
  recordType: HealthRecordType
  description: String
  veterinarianName: String
  medication: String
  withdrawalPeriodDays: Integer
  withdrawalEndDate: LocalDate
  cost: Double
  createdAt: LocalDateTime
}

WithdrawalResponse {
  cowId: Long
  cowTagId: String
  healthRecordId: Long
  withdrawalEndDate: LocalDate
  daysRemaining: Integer
  medication: String
}
```

#### FinancialController
Handles financial transactions and analytics.

**Endpoints:**
- `POST /api/financial/transactions` - Create transaction
- `GET /api/financial/transactions` - List transactions (filter by type, category, date range)
- `GET /api/financial/transactions/{id}` - Get transaction by ID
- `PUT /api/financial/transactions/{id}` - Update transaction
- `DELETE /api/financial/transactions/{id}` - Soft delete transaction
- `GET /api/financial/analytics/profit-loss` - Get profit/loss for period
- `GET /api/financial/analytics/income-breakdown` - Get income by category
- `GET /api/financial/analytics/expense-breakdown` - Get expenses by category
- `GET /api/financial/analytics/trends` - Get monthly financial trends

**Request/Response Models:**
```java
TransactionRequest {
  date: LocalDate (required)
  type: TransactionType (required) // INCOME, EXPENSE
  category: String (required) // MILK_SALES, LIVESTOCK_SALES, FEED, MEDICINE, LABOR, etc.
  amount: Double (required, > 0)
  description: String (required)
  referenceId: String
}

TransactionResponse {
  id: Long
  date: LocalDate
  type: TransactionType
  category: String
  amount: Double
  description: String
  referenceId: String
  deleted: Boolean
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}

ProfitLossResponse {
  startDate: LocalDate
  endDate: LocalDate
  totalIncome: Double
  totalExpenses: Double
  netProfit: Double
  profitMargin: Double
}

CategoryBreakdownResponse {
  category: String
  total: Double
  percentage: Double
  transactionCount: Integer
}
```

#### AnalyticsController
Provides dashboard and cross-module analytics.

**Endpoints:**
- `GET /api/analytics/dashboard` - Get dashboard summary
- `GET /api/analytics/production-comparison` - Compare production across periods

**Request/Response Models:**
```java
DashboardResponse {
  activeCowsCount: Integer
  todayProduction: Double
  monthlyFinancialSummary: MonthlyFinancialSummary
  cowsInWithdrawal: Integer
  upcomingVaccinations: List<UpcomingVaccination>
  productionTrend30Days: List<DailyProduction>
  topProducers: List<TopProducer>
}

MonthlyFinancialSummary {
  month: YearMonth
  totalIncome: Double
  totalExpenses: Double
  netProfit: Double
}
```

#### AuthController
Handles authentication and user management.

**Endpoints:**
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /api/auth/me` - Get current user info

**Request/Response Models:**
```java
RegisterRequest {
  username: String (required, unique, 3-50 chars)
  password: String (required, min 8 chars)
  role: UserRole (default: USER) // ADMIN, MANAGER, USER
  fullName: String
}

LoginRequest {
  username: String (required)
  password: String (required)
}

AuthResponse {
  token: String
  type: String // "Bearer"
  expiresIn: Long
  user: UserInfo
}

UserInfo {
  id: Long
  username: String
  role: UserRole
  fullName: String
}
```

### Service Layer

#### CowService
Business logic for livestock management.

**Methods:**
- `registerCow(CowRegistrationRequest)`: Validates uniqueness of tag ID, creates cow record
- `getCowById(Long)`: Retrieves cow or throws NotFoundException
- `listCows(CowStatus, String breed, Pageable)`: Returns paginated filtered list
- `updateCow(Long, CowUpdateRequest)`: Validates and updates cow
- `deleteCow(Long)`: Checks for dependencies, deletes if safe
- `updateCowStatus(Long, CowStatus)`: Updates status with validation
- `addBreedingRecord(Long, BreedingRecordRequest)`: Creates breeding record
- `getBreedingRecords(Long)`: Returns breeding history for cow

#### MilkProductionService
Business logic for production tracking and analytics.

**Methods:**
- `recordProduction(ProductionRecordRequest)`: Validates uniqueness (cow+date), calculates total
- `getProductionById(Long)`: Retrieves record or throws NotFoundException
- `listProduction(Long cowId, LocalDate start, LocalDate end, Pageable)`: Returns filtered records
- `updateProduction(Long, ProductionRecordRequest)`: Recalculates total on update
- `deleteProduction(Long)`: Deletes production record
- `getProductionTrends(LocalDate start, LocalDate end)`: Aggregates daily totals
- `getCowProductivity()`: Calculates average per cow
- `getTopProducers(Integer limit)`: Returns top N producers

#### HealthService
Business logic for health management.

**Methods:**
- `createHealthRecord(HealthRecordRequest)`: Creates record, calculates withdrawal end date
- `getHealthRecordById(Long)`: Retrieves record or throws NotFoundException
- `listHealthRecords(Long cowId, HealthRecordType type, LocalDate start, LocalDate end, Pageable)`: Returns filtered records
- `updateHealthRecord(Long, HealthRecordRequest)`: Updates and recalculates withdrawal
- `deleteHealthRecord(Long)`: Deletes health record
- `getActiveWithdrawals()`: Returns cows currently in withdrawal with days remaining

#### FinancialService
Business logic for financial management.

**Methods:**
- `createTransaction(TransactionRequest)`: Validates and creates transaction
- `getTransactionById(Long)`: Retrieves transaction or throws NotFoundException
- `listTransactions(TransactionType, String category, LocalDate start, LocalDate end, Pageable)`: Returns filtered transactions
- `updateTransaction(Long, TransactionRequest)`: Updates transaction
- `softDeleteTransaction(Long)`: Marks transaction as deleted
- `calculateProfitLoss(LocalDate start, LocalDate end)`: Aggregates income and expenses
- `getIncomeBreakdown(LocalDate start, LocalDate end)`: Groups income by category
- `getExpenseBreakdown(LocalDate start, LocalDate end)`: Groups expenses by category
- `getFinancialTrends(LocalDate start, LocalDate end)`: Returns monthly aggregates

#### AnalyticsService
Cross-module analytics and dashboard data.

**Methods:**
- `getDashboardData()`: Aggregates data from all modules
- `compareProductionPeriods(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2)`: Compares two time periods

### Repository Layer

All repositories extend `JpaRepository` and use Spring Data JPA query methods.

#### CowRepository
```java
interface CowRepository extends JpaRepository<Cow, Long> {
  Optional<Cow> findByTagId(String tagId);
  List<Cow> findByStatus(CowStatus status, Pageable pageable);
  List<Cow> findByStatusAndBreed(CowStatus status, String breed, Pageable pageable);
  Long countByStatus(CowStatus status);
  boolean existsByTagId(String tagId);
}
```

#### MilkProductionRepository
```java
interface MilkProductionRepository extends JpaRepository<MilkProduction, Long> {
  List<MilkProduction> findByCowIdAndDateBetween(Long cowId, LocalDate start, LocalDate end, Pageable pageable);
  Optional<MilkProduction> findByCowIdAndDate(Long cowId, LocalDate date);
  
  @Query("SELECT new ...ProductionTrendDTO(mp.date, SUM(mp.totalQuantity), AVG(mp.totalQuantity), COUNT(mp)) " +
         "FROM MilkProduction mp WHERE mp.date BETWEEN :start AND :end GROUP BY mp.date ORDER BY mp.date")
  List<ProductionTrendDTO> getProductionTrends(LocalDate start, LocalDate end);
  
  @Query("SELECT new ...CowProductivityDTO(c.id, c.tagId, AVG(mp.totalQuantity)) " +
         "FROM MilkProduction mp JOIN mp.cow c GROUP BY c.id, c.tagId ORDER BY AVG(mp.totalQuantity) DESC")
  List<CowProductivityDTO> getCowProductivity(Pageable pageable);
}
```

#### HealthRecordRepository
```java
interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
  List<HealthRecord> findByCowId(Long cowId, Pageable pageable);
  List<HealthRecord> findByCowIdAndRecordType(Long cowId, HealthRecordType type, Pageable pageable);
  List<HealthRecord> findByDateBetween(LocalDate start, LocalDate end, Pageable pageable);
  
  @Query("SELECT hr FROM HealthRecord hr WHERE hr.withdrawalPeriodDays > 0 " +
         "AND FUNCTION('DATE_ADD', hr.date, hr.withdrawalPeriodDays, 'DAY') >= CURRENT_DATE")
  List<HealthRecord> findActiveWithdrawals();
}
```

#### FinancialTransactionRepository
```java
interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
  List<FinancialTransaction> findByDeletedFalseAndDateBetween(LocalDate start, LocalDate end, Pageable pageable);
  List<FinancialTransaction> findByDeletedFalseAndTypeAndDateBetween(TransactionType type, LocalDate start, LocalDate end, Pageable pageable);
  
  @Query("SELECT SUM(ft.amount) FROM FinancialTransaction ft " +
         "WHERE ft.deleted = false AND ft.type = :type AND ft.date BETWEEN :start AND :end")
  Double sumByTypeAndDateBetween(TransactionType type, LocalDate start, LocalDate end);
  
  @Query("SELECT new ...CategoryBreakdownDTO(ft.category, SUM(ft.amount), COUNT(ft)) " +
         "FROM FinancialTransaction ft WHERE ft.deleted = false AND ft.type = :type " +
         "AND ft.date BETWEEN :start AND :end GROUP BY ft.category")
  List<CategoryBreakdownDTO> getBreakdownByCategory(TransactionType type, LocalDate start, LocalDate end);
}
```

#### UserRepository
```java
interface UserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByUsername(String username);
  boolean existsByUsername(String username);
}
```

## Data Models

### Entity Relationships

```
AppUser (1) ──────────────────────────────────────┐
                                                   │
Cow (1) ────< (N) MilkProduction                  │
  │                                                │
  ├────< (N) HealthRecord                         │
  │                                                │
  └────< (N) BreedingRecord                       │
                                                   │
FinancialTransaction (N) ─────────────────────────┘
```

### Cow Entity
```java
@Entity
@Table(name = "cows")
class Cow {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(unique = true, nullable = false)
  private String tagId;
  
  @Column(nullable = false)
  private String breed;
  
  @Column(nullable = false)
  private LocalDate dateOfBirth;
  
  @Column(nullable = false)
  private LocalDate acquisitionDate;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CowStatus status; // ACTIVE, SOLD, DECEASED
  
  @OneToMany(mappedBy = "cow", cascade = CascadeType.ALL)
  private List<MilkProduction> productionRecords;
  
  @OneToMany(mappedBy = "cow", cascade = CascadeType.ALL)
  private List<HealthRecord> healthRecords;
  
  @OneToMany(mappedBy = "cow", cascade = CascadeType.ALL)
  private List<BreedingRecord> breedingRecords;
  
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;
  
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}

enum CowStatus {
  ACTIVE, SOLD, DECEASED
}
```

### MilkProduction Entity
```java
@Entity
@Table(name = "milk_production", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"cow_id", "date"}))
class MilkProduction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "cow_id", nullable = false)
  private Cow cow;
  
  @Column(nullable = false)
  private LocalDate date;
  
  @Column(nullable = false)
  private Double morningQuantity;
  
  @Column(nullable = false)
  private Double eveningQuantity;
  
  @Column(nullable = false)
  private Double totalQuantity; // Calculated: morning + evening
  
  private String notes;
  
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;
  
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
```

### HealthRecord Entity
```java
@Entity
@Table(name = "health_records")
class HealthRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "cow_id", nullable = false)
  private Cow cow;
  
  @Column(nullable = false)
  private LocalDate date;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private HealthRecordType recordType; // VACCINATION, TREATMENT, CHECKUP
  
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;
  
  private String veterinarianName;
  
  private String medication;
  
  @Column(nullable = false)
  private Integer withdrawalPeriodDays = 0;
  
  private Double cost;
  
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;
  
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}

enum HealthRecordType {
  VACCINATION, TREATMENT, CHECKUP
}
```

### BreedingRecord Entity
```java
@Entity
@Table(name = "breeding_records")
class BreedingRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "cow_id", nullable = false)
  private Cow cow;
  
  @Column(nullable = false)
  private LocalDate breedingDate;
  
  private String bullId;
  
  private LocalDate expectedCalvingDate;
  
  private LocalDate actualCalvingDate;
  
  private String notes;
  
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;
  
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
```

### FinancialTransaction Entity
```java
@Entity
@Table(name = "financial_transactions")
class FinancialTransaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private LocalDate date;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType type; // INCOME, EXPENSE
  
  @Column(nullable = false)
  private String category;
  
  @Column(nullable = false)
  private Double amount;
  
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;
  
  private String referenceId;
  
  @Column(nullable = false)
  private Boolean deleted = false;
  
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;
  
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}

enum TransactionType {
  INCOME, EXPENSE
}
```

### AppUser Entity
```java
@Entity
@Table(name = "users")
class AppUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(unique = true, nullable = false)
  private String username;
  
  @Column(nullable = false)
  private String passwordHash;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role; // ADMIN, MANAGER, USER
  
  private String fullName;
  
  @Column(nullable = false)
  private Boolean active = true;
  
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;
  
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}

enum UserRole {
  ADMIN,   // Full access
  MANAGER, // Read/write access to all modules
  USER     // Read-only access
}
```


## Correctness Properties

A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.

### Property Reflection

After analyzing all acceptance criteria, several redundancies were identified:
- Property 1.6 (prevent cow deletion with dependencies) and 11.2 (referential integrity) test the same behavior
- Property 5.5 (active withdrawals) and 12.4 (dashboard withdrawals) test the same filtering logic
- Property 4.1 (production trends) and 12.6 (dashboard production trends) test the same aggregation

These redundant properties have been consolidated into single comprehensive properties below.

### Livestock Management Properties

**Property 1: Cow registration round-trip**
*For any* valid cow registration data (unique tag ID, breed, dates, status), creating a cow and then retrieving it should return a record with all fields matching the original input.
**Validates: Requirements 1.1, 1.4**

**Property 2: Duplicate tag ID rejection**
*For any* cow that exists in the system, attempting to register another cow with the same tag ID should be rejected with an error.
**Validates: Requirements 1.2**

**Property 3: Status update correctness**
*For any* cow and any valid status value (ACTIVE, SOLD, DECEASED), updating the cow's status should result in the cow having that status when retrieved.
**Validates: Requirements 1.3**

**Property 4: Cow filtering correctness**
*For any* combination of status and breed filters, the returned cow list should contain only cows matching all specified filters.
**Validates: Requirements 1.5**

**Property 5: Referential integrity enforcement**
*For any* cow that has associated production records, health records, or breeding records, attempting to delete the cow should be rejected with an error.
**Validates: Requirements 1.6, 11.2**

**Property 6: Cow update validation**
*For any* cow and any update data, if the update data is valid, the cow should be updated; if invalid, the request should be rejected and the cow should remain unchanged.
**Validates: Requirements 1.7**

### Breeding Management Properties

**Property 7: Breeding record association**
*For any* cow and breeding record data, creating a breeding record should associate it with the specified cow, and retrieving breeding records for that cow should include the created record.
**Validates: Requirements 2.1, 2.3**

**Property 8: Future date rejection**
*For any* breeding record with a breeding date in the future, the creation request should be rejected.
**Validates: Requirements 2.2**

**Property 9: Breeding record update**
*For any* breeding record and valid update data, updating the record should result in the record having the new values when retrieved.
**Validates: Requirements 2.4**

### Milk Production Properties

**Property 10: Production record round-trip**
*For any* valid production record (cow ID, date, morning quantity, evening quantity), creating the record and retrieving it should return all fields matching the original input.
**Validates: Requirements 3.1**

**Property 11: Negative quantity rejection**
*For any* production record with negative morning or evening quantities, the creation request should be rejected.
**Validates: Requirements 3.2**

**Property 12: Total quantity calculation invariant**
*For any* production record, the total quantity should always equal morning quantity plus evening quantity, both at creation and after updates.
**Validates: Requirements 3.3, 3.6**

**Property 13: Cow-date uniqueness**
*For any* cow and date that already has a production record, attempting to create another production record for the same cow and date should be rejected.
**Validates: Requirements 3.4**

**Property 14: Production filtering correctness**
*For any* cow ID and date range filters, the returned production records should contain only records matching the specified cow and falling within the date range.
**Validates: Requirements 3.5**

### Production Analytics Properties

**Property 15: Production trend aggregation**
*For any* date range and set of production records, the aggregated daily totals should equal the sum of all production records for each date.
**Validates: Requirements 4.1, 12.6**

**Property 16: Cow productivity calculation**
*For any* cow with production records, the average daily production should equal the sum of all production totals divided by the number of records.
**Validates: Requirements 4.2**

**Property 17: Top producers ranking**
*For any* set of cows with production records, the top producers list should be sorted in descending order by total production volume.
**Validates: Requirements 4.4**

### Health Management Properties

**Property 18: Health record round-trip**
*For any* valid health record data (cow ID, date, type, description), creating the record and retrieving it should return all fields matching the original input.
**Validates: Requirements 5.1, 5.2**

**Property 19: Health record sorting**
*For any* cow with multiple health records, retrieving the records should return them sorted by date in ascending order.
**Validates: Requirements 5.3**

**Property 20: Withdrawal end date calculation**
*For any* health record with a withdrawal period, the withdrawal end date should equal the record date plus the withdrawal period in days.
**Validates: Requirements 5.4**

**Property 21: Active withdrawal filtering**
*For any* current date, the active withdrawals list should contain only cows with health records where the withdrawal end date is greater than or equal to the current date.
**Validates: Requirements 5.5, 12.4**

### Financial Management Properties

**Property 22: Transaction round-trip**
*For any* valid transaction data (date, type, category, amount, description), creating the transaction and retrieving it should return all fields matching the original input.
**Validates: Requirements 6.1**

**Property 23: Negative amount rejection**
*For any* transaction with a negative amount, the creation request should be rejected.
**Validates: Requirements 6.2**

**Property 24: Transaction filtering correctness**
*For any* date range and type filters, the returned transactions should contain only non-deleted transactions matching the specified filters.
**Validates: Requirements 6.3**

**Property 25: Transaction update validation**
*For any* transaction and valid update data, updating the transaction should result in the transaction having the new values when retrieved.
**Validates: Requirements 6.4**

**Property 26: Soft delete behavior**
*For any* transaction, deleting it should mark it as deleted but not remove it from the database, and it should not appear in subsequent filtered queries.
**Validates: Requirements 6.5**

### Financial Analytics Properties

**Property 27: Profit/loss calculation**
*For any* date range, the profit/loss should equal the sum of all income transactions minus the sum of all expense transactions within that range.
**Validates: Requirements 7.1**

**Property 28: Income breakdown aggregation**
*For any* date range, the sum of all category totals in the income breakdown should equal the total income for that period.
**Validates: Requirements 7.2**

**Property 29: Expense breakdown aggregation**
*For any* date range, the sum of all category totals in the expense breakdown should equal the total expenses for that period.
**Validates: Requirements 7.3**

**Property 30: Financial trends monthly aggregation**
*For any* date range, the monthly financial trends should group transactions by month and sum amounts correctly.
**Validates: Requirements 7.4**

### Authentication and Authorization Properties

**Property 31: User registration with password hashing**
*For any* valid user registration data, creating a user should store the username and role correctly, and the password should be hashed (not stored in plain text).
**Validates: Requirements 8.1, 14.5**

**Property 32: Valid credentials authentication**
*For any* registered user with correct username and password, the login request should return a valid JWT token.
**Validates: Requirements 8.2**

**Property 33: Invalid credentials rejection**
*For any* login request with incorrect username or password, the request should be rejected.
**Validates: Requirements 8.3**

**Property 34: Unauthorized request rejection**
*For any* protected endpoint, a request without a valid JWT token should return a 401 Unauthorized status.
**Validates: Requirements 8.4, 9.3**

**Property 35: Token validation and extraction**
*For any* valid JWT token, the system should correctly extract user information (ID, username, role) from the token.
**Validates: Requirements 8.5**

**Property 36: Role-based access control**
*For any* endpoint with role restrictions, users without the required role should receive a 403 Forbidden response.
**Validates: Requirements 8.6, 9.4**

### Error Handling Properties

**Property 37: Validation error response format**
*For any* request with invalid data, the response should have a 400 status code and include all validation errors in a structured format.
**Validates: Requirements 9.1, 9.6**

**Property 38: Not found error response**
*For any* request for a non-existent resource, the response should have a 404 status code with a descriptive message.
**Validates: Requirements 9.2**

**Property 39: Input sanitization**
*For any* user input containing potentially dangerous characters (XSS patterns), the system should either sanitize or reject the input.
**Validates: Requirements 14.2**

### Dashboard Properties

**Property 40: Active cows count accuracy**
*For any* point in time, the dashboard active cows count should equal the number of cows with status ACTIVE.
**Validates: Requirements 12.1**

**Property 41: Today's production total**
*For any* current date, the dashboard today's production should equal the sum of all production records for that date.
**Validates: Requirements 12.2**

**Property 42: Monthly financial summary**
*For any* current month, the dashboard financial summary should correctly calculate total income, expenses, and net profit for that month.
**Validates: Requirements 12.3**

**Property 43: Upcoming vaccinations filtering**
*For any* current date, the dashboard upcoming vaccinations should include health records of type VACCINATION with dates in the near future.
**Validates: Requirements 12.5**

### Transaction and Concurrency Properties

**Property 44: Transaction atomicity**
*For any* operation that creates multiple related records, if any part fails, all changes should be rolled back and no partial data should be persisted.
**Validates: Requirements 13.2**

**Property 45: Optimistic locking for concurrent updates**
*For any* record being updated concurrently by multiple requests, only one update should succeed and others should receive a conflict error.
**Validates: Requirements 13.3**

**Property 46: Rate limiting on authentication**
*For any* authentication endpoint, excessive requests from the same source within a short time period should be throttled.
**Validates: Requirements 14.4**

### Pagination and Filtering Properties

**Property 47: Pagination correctness**
*For any* list endpoint with page number and page size parameters, the returned results should contain the correct subset of records and include total count.
**Validates: Requirements 15.1, 15.2**

**Property 48: Filter-then-paginate order**
*For any* list request with both filters and pagination, the total count should reflect the filtered results, not the unfiltered dataset.
**Validates: Requirements 15.3**

**Property 49: Sorting correctness**
*For any* list request with sort parameters, the results should be ordered by the specified field in the specified direction.
**Validates: Requirements 15.4**

**Property 50: Foreign key constraint enforcement**
*For any* entity with foreign key relationships, attempting to create a record with an invalid foreign key should be rejected.
**Validates: Requirements 11.1**

**Property 51: Cascade delete behavior**
*For any* entity configured with cascade delete, deleting the parent entity should automatically delete all dependent child entities.
**Validates: Requirements 11.5**

## Error Handling

### Exception Hierarchy

The system uses a custom exception hierarchy for consistent error handling:

```java
ApiException (base)
├── ResourceNotFoundException (404)
├── DuplicateResourceException (409)
├── ValidationException (400)
├── UnauthorizedException (401)
├── ForbiddenException (403)
└── BusinessRuleException (422)
```

### Global Exception Handler

A `@ControllerAdvice` class handles all exceptions and returns consistent error responses:

```java
@ControllerAdvice
class GlobalExceptionHandler {
  
  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(404).body(new ErrorResponse(
      "NOT_FOUND",
      ex.getMessage(),
      LocalDateTime.now()
    ));
  }
  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    List<FieldError> errors = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
      .collect(Collectors.toList());
    
    return ResponseEntity.status(400).body(new ValidationErrorResponse(
      "VALIDATION_FAILED",
      "Request validation failed",
      errors,
      LocalDateTime.now()
    ));
  }
  
  @ExceptionHandler(Exception.class)
  ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    logger.error("Unexpected error", ex);
    return ResponseEntity.status(500).body(new ErrorResponse(
      "INTERNAL_ERROR",
      "An unexpected error occurred",
      LocalDateTime.now()
    ));
  }
}
```

### Error Response Format

All error responses follow a consistent structure:

```java
ErrorResponse {
  code: String        // Error code (e.g., "NOT_FOUND", "VALIDATION_FAILED")
  message: String     // Human-readable error message
  timestamp: LocalDateTime
}

ValidationErrorResponse extends ErrorResponse {
  errors: List<FieldError>
}

FieldError {
  field: String       // Field name that failed validation
  message: String     // Validation error message
}
```

### Validation Strategy

Input validation uses Bean Validation (JSR-380) annotations:

```java
@NotNull(message = "Tag ID is required")
@Size(min = 1, max = 50, message = "Tag ID must be between 1 and 50 characters")
private String tagId;

@NotNull(message = "Date is required")
@PastOrPresent(message = "Date cannot be in the future")
private LocalDate date;

@NotNull(message = "Amount is required")
@Positive(message = "Amount must be positive")
private Double amount;
```

Custom validators for complex business rules:

```java
@Constraint(validatedBy = UniqueCowTagValidator.class)
@interface UniqueCowTag {
  String message() default "Tag ID already exists";
}

@Constraint(validatedBy = CowDateUniqueValidator.class)
@interface UniqueProductionRecord {
  String message() default "Production record already exists for this cow and date";
}
```

## Testing Strategy

### Dual Testing Approach

The system requires both unit tests and property-based tests for comprehensive coverage:

**Unit Tests**: Focus on specific examples, edge cases, and integration points
- Example: Test that a specific cow registration succeeds
- Example: Test that empty tag ID is rejected
- Example: Test that 404 is returned for non-existent cow ID
- Integration tests for controller-service-repository flow

**Property-Based Tests**: Verify universal properties across all inputs
- Generate random valid cow data and verify round-trip consistency
- Generate random production records and verify total calculation invariant
- Generate random date ranges and verify aggregation correctness
- Each property test runs minimum 100 iterations

### Property-Based Testing Configuration

The system uses **JUnit-Quickcheck** for property-based testing in Java:

```xml
<dependency>
  <groupId>com.pholser</groupId>
  <artifactId>junit-quickcheck-core</artifactId>
  <version>1.0</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>com.pholser</groupId>
  <artifactId>junit-quickcheck-generators</artifactId>
  <version>1.0</version>
  <scope>test</scope>
</dependency>
```

Each property test must:
1. Run minimum 100 iterations (configured via `@Property(trials = 100)`)
2. Include a comment tag referencing the design property
3. Use appropriate generators for test data

**Tag Format**: `// Feature: ziwa-dairy-api, Property {number}: {property_text}`

**Example Property Test**:
```java
@RunWith(JUnitQuickcheck.class)
public class CowServicePropertyTest {
  
  // Feature: ziwa-dairy-api, Property 1: Cow registration round-trip
  @Property(trials = 100)
  public void cowRegistrationRoundTrip(
    @From(CowDataGenerator.class) CowRegistrationRequest request
  ) {
    Cow created = cowService.registerCow(request);
    Cow retrieved = cowService.getCowById(created.getId());
    
    assertEquals(request.getTagId(), retrieved.getTagId());
    assertEquals(request.getBreed(), retrieved.getBreed());
    assertEquals(request.getDateOfBirth(), retrieved.getDateOfBirth());
    assertEquals(request.getAcquisitionDate(), retrieved.getAcquisitionDate());
    assertEquals(request.getStatus(), retrieved.getStatus());
  }
  
  // Feature: ziwa-dairy-api, Property 12: Total quantity calculation invariant
  @Property(trials = 100)
  public void totalQuantityInvariant(
    @InRange(minDouble = 0.0, maxDouble = 100.0) double morning,
    @InRange(minDouble = 0.0, maxDouble = 100.0) double evening
  ) {
    ProductionRecordRequest request = new ProductionRecordRequest();
    request.setMorningQuantity(morning);
    request.setEveningQuantity(evening);
    
    MilkProduction record = productionService.recordProduction(request);
    
    assertEquals(morning + evening, record.getTotalQuantity(), 0.001);
  }
}
```

### Test Organization

Tests are organized by layer and module:

```
src/test/java/
├── com.ziwa.dairy.controller/
│   ├── CowControllerTest.java
│   ├── MilkProductionControllerTest.java
│   ├── HealthControllerTest.java
│   ├── FinancialControllerTest.java
│   └── AnalyticsControllerTest.java
├── com.ziwa.dairy.service/
│   ├── CowServiceTest.java
│   ├── CowServicePropertyTest.java
│   ├── MilkProductionServiceTest.java
│   ├── MilkProductionServicePropertyTest.java
│   ├── HealthServiceTest.java
│   ├── HealthServicePropertyTest.java
│   ├── FinancialServiceTest.java
│   ├── FinancialServicePropertyTest.java
│   └── AnalyticsServiceTest.java
├── com.ziwa.dairy.repository/
│   ├── CowRepositoryTest.java
│   ├── MilkProductionRepositoryTest.java
│   ├── HealthRecordRepositoryTest.java
│   └── FinancialTransactionRepositoryTest.java
└── com.ziwa.dairy.integration/
    ├── CowIntegrationTest.java
    ├── ProductionIntegrationTest.java
    ├── HealthIntegrationTest.java
    ├── FinancialIntegrationTest.java
    └── AuthenticationIntegrationTest.java
```

### Testing Guidelines

1. **Unit tests** should cover:
   - Specific business logic examples
   - Edge cases (empty lists, boundary values)
   - Error conditions and exception handling
   - Mock external dependencies

2. **Property tests** should cover:
   - Universal invariants (calculations, relationships)
   - Round-trip properties (create-retrieve, serialize-deserialize)
   - Filtering and aggregation correctness
   - Input validation across all valid/invalid inputs

3. **Integration tests** should cover:
   - End-to-end API flows
   - Database transactions and rollbacks
   - Authentication and authorization
   - Cross-module interactions

4. **Test data generators** should:
   - Generate realistic random data
   - Include edge cases in generation strategy
   - Respect business constraints
   - Be reusable across test classes

### API Documentation Testing

The system should include tests to verify:
- OpenAPI/Swagger documentation is accessible
- All endpoints are documented
- Request/response schemas are complete
- Authentication requirements are specified
- Example requests/responses are provided

This ensures the API documentation remains accurate and useful for frontend developers.
