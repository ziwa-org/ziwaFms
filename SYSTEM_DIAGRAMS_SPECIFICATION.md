# Ziwa Dairy Farm Management System - Diagrams Specification

This document provides comprehensive specifications for creating system diagrams including data flow diagrams (Context Level 1 & 2), activity diagrams, ERD, class diagrams, sequence diagrams, program flowcharts, and user interaction wireframes.

---

## 1. DATA FLOW DIAGRAMS

### 1.1 Context Level 0 (System Context)

**External Entities:**
- Farm Manager (Admin/Manager role)
- Farm Worker (User role)
- Veterinarian (External)
- Financial System (External - future integration)

**System:** Ziwa Dairy Farm Management System

**Data Flows:**
- Farm Manager → System: Authentication credentials, cow registration data, financial transactions, system configurations
- System → Farm Manager: Dashboard analytics, reports, alerts, authentication tokens
- Farm Worker → System: Production records, health observations, authentication credentials
- System → Farm Worker: Daily production summaries, cow status, task lists
- Veterinarian → System: Health records, treatment plans (via Farm Manager)
- System → Veterinarian: Health history, vaccination schedules (via Farm Manager)

### 1.2 Context Level 1 (Major Processes)

**Processes:**
1. **Authentication & Authorization (P1)**
   - Inputs: Username, password, JWT token
   - Outputs: Authentication token, user session, access permissions
   
2. **Livestock Management (P2)**
   - Inputs: Cow registration data, breeding records, status updates
   - Outputs: Cow profiles, breeding history, herd composition reports
   
3. **Production Tracking (P3)**
   - Inputs: Daily milk quantities (morning/evening), production date, cow ID
   - Outputs: Production records, productivity analytics, trend reports

4. **Health Management (P4)**
   - Inputs: Health records, vaccination data, treatment details, withdrawal periods
   - Outputs: Health history, active withdrawals, vaccination schedules
   
5. **Financial Management (P5)**
   - Inputs: Income transactions, expense transactions, categories, amounts
   - Outputs: Profit/loss reports, category breakdowns, financial trends
   
6. **Analytics & Reporting (P6)**
   - Inputs: Date ranges, filter criteria, comparison parameters
   - Outputs: Dashboard summaries, comparative analytics, performance metrics

**Data Stores:**
- D1: Users Database (authentication data)
- D2: Cows Database (livestock information)
- D3: Production Database (milk production records)
- D4: Health Database (medical records)
- D5: Financial Database (transaction records)
- D6: Breeding Database (breeding records)

### 1.3 Context Level 2 (Detailed Process Breakdown)

**P2: Livestock Management - Detailed**
- P2.1: Register New Cow
  - Input: Tag ID, breed, DOB, acquisition date
  - Process: Validate uniqueness, create record
  - Output: Cow profile, confirmation
  
- P2.2: Update Cow Information
  - Input: Cow ID, updated fields
  - Process: Validate data, update record
  - Output: Updated cow profile
  
- P2.3: Manage Cow Status
  - Input: Cow ID, new status (ACTIVE/SOLD/DECEASED)
  - Process: Validate transition, update status
  - Output: Status confirmation

- P2.4: Record Breeding Event
  - Input: Cow ID, breeding date, bull ID, expected calving date
  - Process: Create breeding record, calculate due date
  - Output: Breeding record, calving schedule

**P3: Production Tracking - Detailed**
- P3.1: Record Daily Production
  - Input: Cow ID, date, morning qty, evening qty
  - Process: Validate uniqueness, calculate total, store record
  - Output: Production record confirmation
  
- P3.2: Calculate Production Trends
  - Input: Date range
  - Process: Aggregate daily totals, calculate averages
  - Output: Trend data, charts
  
- P3.3: Identify Top Producers
  - Input: Time period, limit
  - Process: Sum production by cow, rank by total
  - Output: Top producers list

**P4: Health Management - Detailed**
- P4.1: Create Health Record
  - Input: Cow ID, record type, description, medication, withdrawal period
  - Process: Store record, calculate withdrawal end date
  - Output: Health record, withdrawal alert
  
- P4.2: Track Active Withdrawals
  - Input: Current date
  - Process: Query records with active withdrawal periods
  - Output: List of cows in withdrawal with days remaining
  
- P4.3: Schedule Vaccinations
  - Input: Vaccination records, frequency
  - Process: Calculate next due dates
  - Output: Upcoming vaccination schedule

---

## 2. ACTIVITY DIAGRAMS

### 2.1 User Login Activity
```
START
→ User enters username and password
→ System validates credentials
→ [Valid?]
   YES → Generate JWT token
       → Store token in browser
       → Redirect to Dashboard
       → END
   NO → Display error message
      → Return to login form
      → END
```

### 2.2 Record Milk Production Activity
```
START
→ User navigates to Production page
→ User clicks "Record Production"
→ System displays form with active cows
→ User selects cow from dropdown
→ User enters date (defaults to today)
→ User enters morning quantity
→ User enters evening quantity
→ System calculates total (morning + evening)
→ User clicks Submit
→ System validates data
→ [Valid?]
   YES → [Duplicate record?]
         NO → Save production record
            → Display success message
            → Refresh production list
            → END
         YES → Display duplicate error
             → Return to form
             → END
   NO → Display validation errors
      → Return to form
      → END
```

### 2.3 Register New Cow Activity
```
START
→ Manager navigates to Livestock page
→ Manager clicks "Register New Cow"
→ System displays registration form
→ Manager enters tag ID
→ Manager enters breed
→ Manager selects date of birth
→ Manager selects acquisition date
→ Manager selects status (default: ACTIVE)
→ Manager clicks Submit
→ System validates data
→ [Valid?]
   YES → [Tag ID unique?]
         YES → Create cow record
             → Display success message
             → Navigate to cow details
             → END
         NO → Display duplicate tag ID error
            → Return to form
            → END
   NO → Display validation errors
      → Return to form
      → END
```

### 2.4 Create Health Record with Withdrawal Activity
```
START
→ User navigates to Health page
→ User clicks "Create Health Record"
→ System displays health record form
→ User selects cow
→ User selects record type (TREATMENT)
→ User enters description
→ User enters medication name
→ User enters withdrawal period (days)
→ User enters veterinarian name
→ User enters cost
→ User clicks Submit
→ System validates data
→ [Valid?]
   YES → Calculate withdrawal end date (date + withdrawal days)
       → Save health record
       → [Withdrawal period > 0?]
          YES → Add to active withdrawals list
              → Display withdrawal alert
              → END
          NO → Display success message
             → END
   NO → Display validation errors
      → Return to form
      → END
```

---

## 3. ENTITY RELATIONSHIP DIAGRAM (ERD)

### 3.1 Entities and Relationships

**USERS**
- PK: id (BIGINT)
- UK: username (VARCHAR)
- Attributes: password_hash, full_name, role, active, created_at, updated_at, version

**COWS**
- PK: id (BIGINT)
- UK: tag_id (VARCHAR)
- Attributes: breed, date_of_birth, acquisition_date, status, created_at, updated_at, version
- Relationships:
  - ONE cow HAS MANY milk_production records
  - ONE cow HAS MANY health_records
  - ONE cow HAS MANY breeding_records

**MILK_PRODUCTION**
- PK: id (BIGINT)
- FK: cow_id → COWS(id)
- UK: (cow_id, date)
- Attributes: date, morning_quantity, evening_quantity, total_quantity, notes, created_at, updated_at, version
- Relationships:
  - MANY milk_production records BELONG TO ONE cow

**HEALTH_RECORDS**
- PK: id (BIGINT)
- FK: cow_id → COWS(id)
- Attributes: date, record_type, description, veterinarian_name, medication, withdrawal_period_days, cost, created_at, updated_at, version
- Relationships:
  - MANY health_records BELONG TO ONE cow

**BREEDING_RECORDS**
- PK: id (BIGINT)
- FK: cow_id → COWS(id)
- Attributes: breeding_date, bull_id, expected_calving_date, actual_calving_date, notes, created_at, updated_at, version
- Relationships:
  - MANY breeding_records BELONG TO ONE cow

**FINANCIAL_TRANSACTIONS**
- PK: id (BIGINT)
- Attributes: date, type, category, amount, description, reference_id, deleted, created_at, updated_at, version
- Relationships: None (independent entity)

### 3.2 Cardinality Summary
```
COWS (1) ──────< (N) MILK_PRODUCTION
COWS (1) ──────< (N) HEALTH_RECORDS
COWS (1) ──────< (N) BREEDING_RECORDS
USERS (independent)
FINANCIAL_TRANSACTIONS (independent)
```

### 3.3 Key Constraints
- CASCADE DELETE: When a cow is deleted, all related production, health, and breeding records are deleted
- UNIQUE CONSTRAINT: (cow_id, date) in MILK_PRODUCTION prevents duplicate daily records
- UNIQUE CONSTRAINT: tag_id in COWS ensures unique identification
- UNIQUE CONSTRAINT: username in USERS ensures unique login credentials
- CHECK CONSTRAINT: morning_quantity >= 0, evening_quantity >= 0
- CHECK CONSTRAINT: amount > 0 in FINANCIAL_TRANSACTIONS
- CHECK CONSTRAINT: withdrawal_period_days >= 0

---

## 4. CLASS DIAGRAM

### 4.1 Backend Class Structure

**Entity Classes:**

```
┌─────────────────────────┐
│      AppUser            │
├─────────────────────────┤
│ - id: Long              │
│ - username: String      │
│ - passwordHash: String  │
│ - fullName: String      │
│ - role: UserRole        │
│ - active: Boolean       │
│ - createdAt: DateTime   │
│ - updatedAt: DateTime   │
│ - version: Long         │
└─────────────────────────┘

┌─────────────────────────┐
│         Cow             │
├─────────────────────────┤
│ - id: Long              │
│ - tagId: String         │
│ - breed: String         │
│ - dateOfBirth: Date     │
│ - acquisitionDate: Date │
│ - status: CowStatus     │
│ - createdAt: DateTime   │
│ - updatedAt: DateTime   │
│ - version: Long         │
├─────────────────────────┤
│ + getProductionRecords()│
│ + getHealthRecords()    │
│ + getBreedingRecords()  │
└─────────────────────────┘
         │
         │ 1
         │
         │ *
    ┌────┴────┬────────────┬──────────────┐
    │         │            │              │
┌───▼──────┐ ┌▼─────────┐ ┌▼────────────┐│
│MilkProd. │ │HealthRec.│ │BreedingRec. ││
└──────────┘ └──────────┘ └─────────────┘│
```

**Service Layer Classes:**

```
┌──────────────────────────┐
│    CowService            │
├──────────────────────────┤
│ - cowRepository          │
│ - breedingRepository     │
├──────────────────────────┤
│ + registerCow()          │
│ + getCowById()           │
│ + listCows()             │
│ + updateCow()            │
│ + deleteCow()            │
│ + updateCowStatus()      │
│ + addBreedingRecord()    │
└──────────────────────────┘

┌──────────────────────────┐
│ MilkProductionService    │
├──────────────────────────┤
│ - productionRepository   │
│ - cowRepository          │
├──────────────────────────┤
│ + recordProduction()     │
│ + getProductionById()    │
│ + listProduction()       │
│ + updateProduction()     │
│ + deleteProduction()     │
│ + getProductionTrends()  │
│ + getCowProductivity()   │
│ + getTopProducers()      │
└──────────────────────────┘
```

**Controller Layer Classes:**

```
┌──────────────────────────┐
│   CowController          │
├──────────────────────────┤
│ - cowService             │
├──────────────────────────┤
│ + registerCow()          │
│ + getCowById()           │
│ + listCows()             │
│ + updateCow()            │
│ + deleteCow()            │
│ + updateCowStatus()      │
│ + addBreedingRecord()    │
│ + getBreedingRecords()   │
└──────────────────────────┘
         │
         │ uses
         ▼
┌──────────────────────────┐
│    CowService            │
└──────────────────────────┘
         │
         │ uses
         ▼
┌──────────────────────────┐
│   CowRepository          │
└──────────────────────────┘
```

### 4.2 Frontend Class Structure

**React Components Hierarchy:**

```
App
├── AuthProvider (Context)
├── Router
    ├── LoginPage
    ├── RegisterPage
    └── Layout
        ├── Sidebar
        ├── Header
        └── Routes
            ├── DashboardPage
            │   ├── MetricCard (x4)
            │   ├── ProductionTrendChart
            │   └── TopProducersTable
            ├── LivestockPage
            │   ├── FilterSection
            │   ├── DataTable
            │   └── CowForm (Dialog)
            ├── ProductionPage
            │   ├── FilterSection
            │   ├── DataTable
            │   ├── ProductionRecordForm
            │   └── ProductionTrendChart
            ├── HealthPage
            │   ├── FilterSection
            │   ├── DataTable
            │   ├── HealthRecordForm
            │   └── WithdrawalPeriodCard
            ├── FinancialPage
            │   ├── FilterSection
            │   ├── DataTable
            │   ├── TransactionForm
            │   ├── FinancialBreakdownChart
            │   └── FinancialTrendChart
            └── AnalyticsPage
                ├── ProductionComparisonTool
                └── ComparisonChart
```

**Service Classes:**

```
┌──────────────────────────┐
│   ApiClient              │
├──────────────────────────┤
│ - baseURL                │
│ - timeout                │
│ - interceptors           │
├──────────────────────────┤
│ + request()              │
│ + get()                  │
│ + post()                 │
│ + put()                  │
│ + delete()               │
└──────────────────────────┘
         ▲
         │ extends
         │
    ┌────┴────┬────────────┬──────────────┐
    │         │            │              │
┌───┴──────┐ ┌┴─────────┐ ┌┴────────────┐│
│CowService│ │ProdServ. │ │HealthServ.  ││
└──────────┘ └──────────┘ └─────────────┘│
```

---

## 5. SEQUENCE DIAGRAMS

### 5.1 User Authentication Sequence

```
User          Frontend       AuthController    UserService      Database      JwtProvider
 │                │                │               │               │               │
 │──Login Form───>│                │               │               │               │
 │                │──POST /login──>│               │               │               │
 │                │                │──validate()──>│               │               │
 │                │                │               │──findByUser──>│               │
 │                │                │               │<──User────────│               │
 │                │                │<──User────────│               │               │
 │                │                │──generate()──────────────────>│               │
 │                │                │<──JWT Token───────────────────│               │
 │                │<──AuthResp.────│               │               │               │
 │<──JWT Token────│                │               │               │               │
 │                │──Store Token───│               │               │               │
 │                │──Navigate──────│               │               │               │
```

### 5.2 Record Milk Production Sequence

```
User      Frontend    ProductionCtrl   ProductionSvc   CowRepo   ProdRepo   Database
 │            │              │               │            │         │          │
 │──Select───>│              │               │            │         │          │
 │   Cow      │──GET /cows──>│               │            │         │          │
 │            │<──Cow List───│               │            │         │          │
 │──Enter────>│              │               │            │         │          │
 │  Data      │              │               │            │         │          │
 │──Submit───>│──POST────────>│               │            │         │          │
 │            │  /production │──validate()───>│            │         │          │
 │            │              │               │──findById─>│         │          │
 │            │              │               │<──Cow──────│         │          │
 │            │              │               │──checkDup──────────>│          │
 │            │              │               │<──None──────────────│          │
 │            │              │               │──save()─────────────────────>│
 │            │              │               │<──Record────────────────────│
 │            │              │<──ProdResp.───│            │         │          │
 │            │<──201────────│               │            │         │          │
 │<──Success──│              │               │            │         │          │
```

### 5.3 Create Health Record with Withdrawal Sequence

```
User      Frontend    HealthCtrl    HealthSvc    CowRepo   HealthRepo   Database
 │            │            │            │           │          │           │
 │──Form─────>│            │            │           │          │           │
 │──Submit───>│──POST──────>│            │           │          │           │
 │            │  /health   │──validate─>│           │          │           │
 │            │            │            │──findCow─>│          │           │
 │            │            │            │<──Cow─────│          │           │
 │            │            │            │──calcEnd──│          │           │
 │            │            │            │  Date     │          │           │
 │            │            │            │──save()───────────>│           │
 │            │            │            │<──Record──────────│           │
 │            │            │<──Response─│           │          │           │
 │            │<──201──────│            │           │          │           │
 │<──Success──│            │            │           │          │           │
 │  +Alert    │            │            │           │          │           │
```

---

## 6. PROGRAM FLOWCHARTS

### 6.1 Production Record Creation Flowchart

```
START
  │
  ▼
[Receive Production Request]
  │
  ▼
[Extract: cowId, date, morningQty, eveningQty]
  │
  ▼
<Validate Input>
  │
  ├─NO─> [Return 400 Bad Request]──> END
  │
  YES
  │
  ▼
<Check Cow Exists?>
  │
  ├─NO─> [Return 404 Not Found]──> END
  │
  YES
  │
  ▼
<Check Duplicate (cowId + date)?>
  │
  ├─YES─> [Return 409 Conflict]──> END
  │
  NO
  │
  ▼
[Calculate totalQty = morningQty + eveningQty]
  │
  ▼
[Create Production Record]
  │
  ▼
[Save to Database]
  │
  ▼
[Return 201 Created with Record]
  │
  ▼
END
```

### 6.2 Financial Profit/Loss Calculation Flowchart

```
START
  │
  ▼
[Receive: startDate, endDate]
  │
  ▼
<Validate Date Range?>
  │
  ├─NO─> [Return 400 Bad Request]──> END
  │
  YES
  │
  ▼
[Query INCOME transactions in date range]
  │
  ▼
[Sum all INCOME amounts → totalIncome]
  │
  ▼
[Query EXPENSE transactions in date range]
  │
  ▼
[Sum all EXPENSE amounts → totalExpenses]
  │
  ▼
[Calculate netProfit = totalIncome - totalExpenses]
  │
  ▼
<totalIncome > 0?>
  │
  ├─YES─> [Calculate profitMargin = (netProfit/totalIncome) * 100]
  │         │
  │         ▼
  │       [Create ProfitLossResponse]
  │         │
  ├─NO──────┘
  │
  ▼
[Return 200 OK with Response]
  │
  ▼
END
```

### 6.3 Active Withdrawal Tracking Flowchart

```
START
  │
  ▼
[Get Current Date]
  │
  ▼
[Query Health Records WHERE withdrawalPeriodDays > 0]
  │
  ▼
[Initialize: activeWithdrawals = []]
  │
  ▼
[FOR EACH health record]
  │
  ▼
[Calculate withdrawalEndDate = record.date + withdrawalPeriodDays]
  │
  ▼
<withdrawalEndDate >= currentDate?>
  │
  ├─YES─> [Calculate daysRemaining = withdrawalEndDate - currentDate]
  │         │
  │         ▼
  │       [Add to activeWithdrawals list]
  │         │
  ├─NO──────┘
  │
  ▼
<More records?>
  │
  ├─YES─> [Loop back to FOR EACH]
  │
  NO
  │
  ▼
[Sort by daysRemaining ASC]
  │
  ▼
[Return activeWithdrawals list]
  │
  ▼
END
```

---

## 7. USER INTERACTION DIAGRAMS / WIREFRAMES

### 7.1 Login Page Wireframe

```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│                  [Ziwa Dairy Logo]                      │
│                                                         │
│              Dairy Farm Management System               │
│                                                         │
│    ┌───────────────────────────────────────────┐       │
│    │  Username: [________________]             │       │
│    │                                            │       │
│    │  Password: [________________]             │       │
│    │                                            │       │
│    │           [  Login  ]                     │       │
│    │                                            │       │
│    │  Don't have an account? Register          │       │
│    └───────────────────────────────────────────┘       │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### 7.2 Dashboard Page Wireframe

```
┌──────────────────────────────────────────────────────────────────────┐
│ [☰] Ziwa Dairy                    Welcome, John Doe [Manager] Logout │
├──────┬───────────────────────────────────────────────────────────────┤
│      │                                                               │
│ [🏠] │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│ Dash │  │ 🐄       │ │ 🥛       │ │ 💰       │ │ ⚠️       │       │
│      │  │ Active   │ │ Today's  │ │ Monthly  │ │ Cows in  │       │
│ [🐄] │  │ Cows     │ │ Prod.    │ │ Profit   │ │ Withdraw │       │
│ Live │  │   125    │ │ 2,450 L  │ │ $12,500  │ │    3     │       │
│      │  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
│ [📊] │                                                               │
│ Prod │  Production Trends (Last 30 Days)                            │
│      │  ┌─────────────────────────────────────────────────┐        │
│ [🏥] │  │                                                  │        │
│ Heal │  │     [Line Chart: Date vs Liters]                │        │
│      │  │                                                  │        │
│ [💰] │  └─────────────────────────────────────────────────┘        │
│ Fina │                                                               │
│      │  Top Producers                                                │
│ [📈] │  ┌─────────────────────────────────────────────────┐        │
│ Anal │  │ Rank │ Tag ID │ Total Prod │ Avg Daily         │        │
│      │  ├──────┼────────┼────────────┼──────────         │        │
│      │  │  1   │ C-001  │  1,250 L   │  41.7 L           │        │
│      │  │  2   │ C-045  │  1,180 L   │  39.3 L           │        │
│      │  │  3   │ C-023  │  1,150 L   │  38.3 L           │        │
│      │  └─────────────────────────────────────────────────┘        │
│      │                                                               │
└──────┴───────────────────────────────────────────────────────────────┘
```

### 7.3 Livestock Management Page Wireframe

```
┌──────────────────────────────────────────────────────────────────────┐
│ [☰] Ziwa Dairy                    Welcome, John Doe [Manager] Logout │
├──────┬───────────────────────────────────────────────────────────────┤
│      │  Livestock Management                                         │
│ [🏠] │                                                               │
│ Dash │  Filters: [Status: All ▼] [Breed: All ▼]  [Register New Cow]│
│      │                                                               │
│ [🐄] │  ┌─────────────────────────────────────────────────────────┐ │
│ Live │  │ Tag ID │ Breed    │ DOB        │ Status  │ Actions     │ │
│ ★    │  ├────────┼──────────┼────────────┼─────────┼─────────    │ │
│      │  │ C-001  │ Holstein │ 2020-03-15 │ ACTIVE  │ [View][Edit]│ │
│ [📊] │  │ C-002  │ Jersey   │ 2019-11-20 │ ACTIVE  │ [View][Edit]│ │
│ Prod │  │ C-003  │ Holstein │ 2021-01-10 │ ACTIVE  │ [View][Edit]│ │
│      │  │ C-004  │ Guernsey │ 2020-07-22 │ SOLD    │ [View]      │ │
│ [🏥] │  │ ...    │ ...      │ ...        │ ...     │ ...         │ │
│ Heal │  └─────────────────────────────────────────────────────────┘ │
│      │                                                               │
│ [💰] │  Showing 1-20 of 125    [< Prev] [1][2][3]...[7] [Next >]   │
│ Fina │                                                               │
│      │                                                               │
│ [📈] │                                                               │
│ Anal │                                                               │
│      │                                                               │
└──────┴───────────────────────────────────────────────────────────────┘
```

### 7.4 Production Recording Form Wireframe

```
┌─────────────────────────────────────────────────────────┐
│  Record Milk Production                          [X]    │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Cow: [Select Cow ▼]                                   │
│       C-001 - Holstein                                  │
│       C-002 - Jersey                                    │
│       ...                                               │
│                                                         │
│  Date: [📅 2024-04-14]                                 │
│                                                         │
│  Morning Quantity (L): [_______]                       │
│                                                         │
│  Evening Quantity (L): [_______]                       │
│                                                         │
│  Total: 0.0 L (auto-calculated)                        │
│                                                         │
│  Notes (optional):                                      │
│  ┌─────────────────────────────────────────────┐       │
│  │                                              │       │
│  └─────────────────────────────────────────────┘       │
│                                                         │
│              [Cancel]  [Submit]                        │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### 7.5 Health Records Page Wireframe

```
┌──────────────────────────────────────────────────────────────────────┐
│ [☰] Ziwa Dairy                    Welcome, John Doe [Manager] Logout │
├──────┬───────────────────────────────────────────────────────────────┤
│      │  Health Management                                            │
│ [🏠] │                                                               │
│ Dash │  [Create Health Record]                                       │
│      │                                                               │
│ [🐄] │  Active Withdrawals (3)                                       │
│ Live │  ┌─────────────────────────────────────────────────────────┐ │
│      │  │ Tag ID │ Medication  │ End Date   │ Days Left │ Status  │ │
│ [📊] │  ├────────┼─────────────┼────────────┼───────────┼─────────│ │
│ Prod │  │ C-023  │ Antibiotic  │ 2024-04-16 │    2      │ 🔴      │ │
│      │  │ C-045  │ Vaccine     │ 2024-04-20 │    6      │ 🟡      │ │
│ [🏥] │  │ C-089  │ Treatment   │ 2024-04-25 │   11      │ 🟢      │ │
│ Heal │  └─────────────────────────────────────────────────────────┘ │
│ ★    │                                                               │
│      │  Health Records                                               │
│ [💰] │  Filters: [Cow: All ▼] [Type: All ▼] [Date Range: ___]      │
│ Fina │  ┌─────────────────────────────────────────────────────────┐ │
│      │  │ Date       │ Cow   │ Type        │ Description │ Cost   │ │
│ [📈] │  ├────────────┼───────┼─────────────┼─────────────┼────────│ │
│ Anal │  │ 2024-04-10 │ C-023 │ TREATMENT   │ Mastitis    │ $45.00 │ │
│      │  │ 2024-04-08 │ C-045 │ VACCINATION │ Annual shot │ $25.00 │ │
│      │  │ 2024-04-05 │ C-012 │ CHECKUP     │ Routine     │ $30.00 │ │
│      │  └─────────────────────────────────────────────────────────┘ │
└──────┴───────────────────────────────────────────────────────────────┘
```

### 7.6 Financial Management Page Wireframe

```
┌──────────────────────────────────────────────────────────────────────┐
│ [☰] Ziwa Dairy                    Welcome, John Doe [Manager] Logout │
├──────┬───────────────────────────────────────────────────────────────┤
│      │  Financial Management                                         │
│ [🏠] │                                                               │
│ Dash │  Date Range: [2024-01-01] to [2024-04-14]  [Record Trans.]  │
│      │                                                               │
│ [🐄] │  ┌──────────┐ ┌──────────┐ ┌──────────┐                     │
│ Live │  │ Income   │ │ Expenses │ │ Net      │                     │
│      │  │ $45,000  │ │ $32,500  │ │ $12,500  │                     │
│ [📊] │  └──────────┘ └──────────┘ └──────────┘                     │
│ Prod │                                                               │
│      │  ┌─────────────────────┐  ┌─────────────────────┐           │
│ [🏥] │  │ Income Breakdown    │  │ Expense Breakdown   │           │
│ Heal │  │                     │  │                     │           │
│      │  │  [Pie Chart]        │  │  [Pie Chart]        │           │
│ [💰] │  │  - Milk Sales 80%   │  │  - Feed 45%         │           │
│ Fina │  │  - Livestock 15%    │  │  - Medicine 20%     │           │
│ ★    │  │  - Other 5%         │  │  - Labor 25%        │           │
│      │  │                     │  │  - Other 10%        │           │
│ [📈] │  └─────────────────────┘  └─────────────────────┘           │
│ Anal │                                                               │
│      │  Recent Transactions                                          │
│      │  ┌─────────────────────────────────────────────────────────┐ │
│      │  │ Date       │ Type    │ Category    │ Amount  │ Actions  │ │
│      │  ├────────────┼─────────┼─────────────┼─────────┼──────────│ │
│      │  │ 2024-04-14 │ INCOME  │ Milk Sales  │ $1,250  │ [Edit]   │ │
│      │  │ 2024-04-13 │ EXPENSE │ Feed        │  $450   │ [Edit]   │ │
│      │  │ 2024-04-12 │ INCOME  │ Milk Sales  │ $1,180  │ [Edit]   │ │
│      │  └─────────────────────────────────────────────────────────┘ │
└──────┴───────────────────────────────────────────────────────────────┘
```

### 7.7 Analytics Page Wireframe

```
┌──────────────────────────────────────────────────────────────────────┐
│ [☰] Ziwa Dairy                    Welcome, John Doe [Manager] Logout │
├──────┬───────────────────────────────────────────────────────────────┤
│      │  Analytics & Reporting                                        │
│ [🏠] │                                                               │
│ Dash │  Production Comparison                                        │
│      │  Period 1: [2024-01-01] to [2024-01-31]                      │
│ [🐄] │  Period 2: [2024-02-01] to [2024-02-29]                      │
│ Live │                                                               │
│      │  ┌──────────────────────┐  ┌──────────────────────┐         │
│ [📊] │  │ Period 1             │  │ Period 2             │         │
│ Prod │  │ Total: 75,000 L      │  │ Total: 78,500 L      │         │
│      │  │ Avg Daily: 2,419 L   │  │ Avg Daily: 2,707 L   │         │
│ [🏥] │  │ Records: 31          │  │ Records: 29          │         │
│ Heal │  └──────────────────────┘  └──────────────────────┘         │
│      │                                                               │
│ [💰] │  Change: +4.7% (↑ 3,500 L)                                   │
│ Fina │                                                               │
│      │  ┌─────────────────────────────────────────────────────────┐ │
│ [📈] │  │                                                          │ │
│ Anal │  │     [Bar Chart: Period 1 vs Period 2]                   │ │
│ ★    │  │                                                          │ │
│      │  └─────────────────────────────────────────────────────────┘ │
│      │                                                               │
│      │  Herd Composition                                             │
│      │  ┌─────────────────────────────────────────────────────────┐ │
│      │  │ Holstein: 65 (52%)  │  Jersey: 40 (32%)                 │ │
│      │  │ Guernsey: 15 (12%)  │  Other: 5 (4%)                    │ │
│      │  └─────────────────────────────────────────────────────────┘ │
└──────┴───────────────────────────────────────────────────────────────┘
```

---

**End of Diagrams Specification Document**
