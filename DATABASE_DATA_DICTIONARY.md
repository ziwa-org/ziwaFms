# Ziwa Dairy Farm Management System - Database Data Dictionary

This document provides a comprehensive data dictionary for all database tables, including field definitions, data types, constraints, and descriptions.

---

## Table: USERS

**Purpose:** Stores user authentication and authorization information for system access control.

| Field Name     | Data Type      | Constraints                    | Description                                                                 |
|----------------|----------------|--------------------------------|-----------------------------------------------------------------------------|
| id             | BIGINT         | PRIMARY KEY, AUTO_INCREMENT    | Unique identifier for each user                                             |
| username       | VARCHAR(50)    | UNIQUE, NOT NULL               | Unique username for login authentication                                    |
| password_hash  | VARCHAR(255)   | NOT NULL                       | Bcrypt hashed password for secure authentication                            |
| full_name      | VARCHAR(100)   | NULL                           | User's full name for display purposes                                       |
| role           | VARCHAR(20)    | NOT NULL                       | User role: ADMIN, MANAGER, or USER (determines access permissions)          |
| active         | BOOLEAN        | NOT NULL, DEFAULT TRUE         | Indicates if the user account is active and can log in                      |
| created_at     | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME | Timestamp when the user account was created                                 |
| updated_at     | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME | Timestamp when the user account was last updated                            |
| version        | BIGINT         | NULL                           | Optimistic locking version number for concurrent update control             |

**Indexes:**
- PRIMARY KEY on `id`
- UNIQUE INDEX on `username`

**Business Rules:**
- Username must be unique across all users
- Password must be hashed using bcrypt before storage
- Role determines access level: ADMIN (full access), MANAGER (read/write), USER (read-only)
- Inactive users cannot authenticate

---

## Table: COWS

**Purpose:** Stores individual livestock information for tracking and management of dairy cattle.

| Field Name        | Data Type      | Constraints                    | Description                                                                 |
|-------------------|----------------|--------------------------------|-----------------------------------------------------------------------------|
| id                | BIGINT         | PRIMARY KEY, AUTO_INCREMENT    | Unique identifier for each cow                                              |
| tag_id            | VARCHAR(50)    | UNIQUE, NOT NULL               | Physical tag identifier attached to the cow (e.g., ear tag number)          |
| breed             | VARCHAR(100)   | NOT NULL                       | Breed of the cow (e.g., Holstein, Jersey, Guernsey)                         |
| date_of_birth     | DATE           | NOT NULL                       | Date when the cow was born                                                  |
| acquisition_date  | DATE           | NOT NULL                       | Date when the cow was acquired by the farm                                  |
| status            | VARCHAR(20)    | NOT NULL, DEFAULT 'ACTIVE'     | Current status: ACTIVE (in herd), SOLD (sold), or DECEASED (died)           |
| created_at        | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME | Timestamp when the cow record was created                                   |
| updated_at        | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME | Timestamp when the cow record was last updated                              |
| version           | BIGINT         | NULL                           | Optimistic locking version number for concurrent update control             |

**Indexes:**
- PRIMARY KEY on `id`
- UNIQUE INDEX on `tag_id`
- INDEX on `status`

**Business Rules:**
- Tag ID must be unique across all cows
- Date of birth cannot be in the future
- Acquisition date cannot be before date of birth
- Status transitions: ACTIVE → SOLD or ACTIVE → DECEASED (no reverse transitions)
- Cows with associated records (production, health, breeding) cannot be deleted

**Relationships:**
- ONE cow HAS MANY milk_production records (1:N)
- ONE cow HAS MANY health_records (1:N)
- ONE cow HAS MANY breeding_records (1:N)

---

## Table: MILK_PRODUCTION

**Purpose:** Records daily milk production data for each cow to track productivity and identify trends.

| Field Name        | Data Type      | Constraints                           | Description                                                                 |
|-------------------|----------------|---------------------------------------|-----------------------------------------------------------------------------|
| id                | BIGINT         | PRIMARY KEY, AUTO_INCREMENT           | Unique identifier for each production record                                |
| cow_id            | BIGINT         | FOREIGN KEY → cows(id), NOT NULL      | Reference to the cow that produced the milk                                 |
| date              | DATE           | NOT NULL                              | Date of milk production                                                     |
| morning_quantity  | DOUBLE         | NOT NULL, CHECK >= 0                  | Quantity of milk produced in morning milking session (liters)               |
| evening_quantity  | DOUBLE         | NOT NULL, CHECK >= 0                  | Quantity of milk produced in evening milking session (liters)               |
| total_quantity    | DOUBLE         | NOT NULL, CHECK >= 0                  | Total daily production (morning + evening) in liters                        |
| notes             | TEXT           | NULL                                  | Optional notes about production (e.g., quality issues, unusual observations)|
| created_at        | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME        | Timestamp when the production record was created                            |
| updated_at        | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME        | Timestamp when the production record was last updated                       |
| version           | BIGINT         | NULL                                  | Optimistic locking version number for concurrent update control             |

**Indexes:**
- PRIMARY KEY on `id`
- INDEX on `cow_id`
- INDEX on `date`
- UNIQUE INDEX on `(cow_id, date)` - prevents duplicate daily records

**Business Rules:**
- Each cow can have only one production record per date
- Morning and evening quantities must be non-negative
- Total quantity is automatically calculated as morning + evening
- Date cannot be in the future
- Production records can only be created for ACTIVE cows

**Relationships:**
- MANY milk_production records BELONG TO ONE cow (N:1)

**Cascade Behavior:**
- ON DELETE CASCADE: When a cow is deleted, all associated production records are deleted

---

## Table: HEALTH_RECORDS

**Purpose:** Tracks medical history including vaccinations, treatments, and checkups for animal welfare and compliance.

| Field Name              | Data Type      | Constraints                           | Description                                                                 |
|-------------------------|----------------|---------------------------------------|-----------------------------------------------------------------------------|
| id                      | BIGINT         | PRIMARY KEY, AUTO_INCREMENT           | Unique identifier for each health record                                    |
| cow_id                  | BIGINT         | FOREIGN KEY → cows(id), NOT NULL      | Reference to the cow receiving medical attention                            |
| date                    | DATE           | NOT NULL                              | Date of the health event (vaccination, treatment, or checkup)               |
| record_type             | VARCHAR(20)    | NOT NULL                              | Type of health record: VACCINATION, TREATMENT, or CHECKUP                   |
| description             | TEXT           | NOT NULL                              | Detailed description of the health event, diagnosis, or procedure           |
| veterinarian_name       | VARCHAR(100)   | NULL                                  | Name of the veterinarian who performed the service                          |
| medication              | VARCHAR(255)   | NULL                                  | Name of medication or vaccine administered                                  |
| withdrawal_period_days  | INTEGER        | NOT NULL, DEFAULT 0, CHECK >= 0       | Number of days milk cannot be sold after treatment (regulatory compliance)  |
| cost                    | DOUBLE         | NULL, CHECK >= 0                      | Cost of the health service in local currency                                |
| created_at              | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME        | Timestamp when the health record was created                                |
| updated_at              | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME        | Timestamp when the health record was last updated                           |
| version                 | BIGINT         | NULL                                  | Optimistic locking version number for concurrent update control             |

**Indexes:**
- PRIMARY KEY on `id`
- INDEX on `cow_id`
- INDEX on `date`
- INDEX on `record_type`

**Business Rules:**
- Date cannot be in the future
- Withdrawal period must be non-negative integer
- Withdrawal end date is calculated as: date + withdrawal_period_days
- Cost must be non-negative if provided
- TREATMENT records typically have withdrawal periods; VACCINATION and CHECKUP usually don't

**Relationships:**
- MANY health_records BELONG TO ONE cow (N:1)

**Cascade Behavior:**
- ON DELETE CASCADE: When a cow is deleted, all associated health records are deleted

**Derived Fields:**
- withdrawal_end_date (calculated): date + withdrawal_period_days
- days_remaining (calculated): withdrawal_end_date - current_date

---

## Table: BREEDING_RECORDS

**Purpose:** Tracks breeding events and calving information for reproduction management and herd planning.

| Field Name              | Data Type      | Constraints                           | Description                                                                 |
|-------------------------|----------------|---------------------------------------|-----------------------------------------------------------------------------|
| id                      | BIGINT         | PRIMARY KEY, AUTO_INCREMENT           | Unique identifier for each breeding record                                  |
| cow_id                  | BIGINT         | FOREIGN KEY → cows(id), NOT NULL      | Reference to the cow that was bred                                          |
| breeding_date           | DATE           | NOT NULL                              | Date when the cow was bred                                                  |
| bull_id                 | VARCHAR(50)    | NULL                                  | Identifier of the bull used for breeding                                    |
| expected_calving_date   | DATE           | NULL                                  | Calculated or estimated date when calf is expected (breeding_date + ~280d)  |
| actual_calving_date     | DATE           | NULL                                  | Actual date when the calf was born (filled after calving)                   |
| notes                   | TEXT           | NULL                                  | Additional notes about breeding or calving (e.g., complications, calf info) |
| created_at              | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME        | Timestamp when the breeding record was created                              |
| updated_at              | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME        | Timestamp when the breeding record was last updated                         |
| version                 | BIGINT         | NULL                                  | Optimistic locking version number for concurrent update control             |

**Indexes:**
- PRIMARY KEY on `id`
- INDEX on `cow_id`

**Business Rules:**
- Breeding date cannot be in the future
- Expected calving date is typically breeding_date + 280 days (average gestation period)
- Actual calving date is filled after the calf is born
- Multiple breeding records can exist for the same cow (different breeding events)

**Relationships:**
- MANY breeding_records BELONG TO ONE cow (N:1)

**Cascade Behavior:**
- ON DELETE CASCADE: When a cow is deleted, all associated breeding records are deleted

---

## Table: FINANCIAL_TRANSACTIONS

**Purpose:** Records all farm income and expenses for financial tracking, profitability analysis, and budgeting.

| Field Name     | Data Type      | Constraints                    | Description                                                                 |
|----------------|----------------|--------------------------------|-----------------------------------------------------------------------------|
| id             | BIGINT         | PRIMARY KEY, AUTO_INCREMENT    | Unique identifier for each financial transaction                            |
| date           | DATE           | NOT NULL                       | Date when the transaction occurred                                          |
| type           | VARCHAR(20)    | NOT NULL                       | Transaction type: INCOME (money received) or EXPENSE (money spent)          |
| category       | VARCHAR(100)   | NOT NULL                       | Category of transaction (e.g., MILK_SALES, FEED, MEDICINE, LABOR)          |
| amount         | DOUBLE         | NOT NULL, CHECK > 0            | Transaction amount in local currency (must be positive)                     |
| description    | TEXT           | NOT NULL                       | Detailed description of the transaction                                     |
| reference_id   | VARCHAR(100)   | NULL                           | Optional reference number (e.g., invoice number, receipt number)            |
| deleted        | BOOLEAN        | NOT NULL, DEFAULT FALSE        | Soft delete flag - marks transaction as deleted without removing from DB    |
| created_at     | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME | Timestamp when the transaction record was created                           |
| updated_at     | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIME | Timestamp when the transaction record was last updated                      |
| version        | BIGINT         | NULL                           | Optimistic locking version number for concurrent update control             |

**Indexes:**
- PRIMARY KEY on `id`
- INDEX on `date`
- INDEX on `type`
- INDEX on `category`

**Business Rules:**
- Amount must be positive (negative values not allowed)
- Date cannot be in the future
- Deleted transactions are excluded from financial calculations and reports
- Soft delete preserves transaction history for audit purposes

**Common Categories:**
- INCOME: MILK_SALES, LIVESTOCK_SALES, CALF_SALES, MANURE_SALES, OTHER_INCOME
- EXPENSE: FEED, MEDICINE, VETERINARY, LABOR, EQUIPMENT, MAINTENANCE, UTILITIES, OTHER_EXPENSE

**Relationships:**
- Independent entity (no foreign key relationships)

**Cascade Behavior:**
- N/A (no foreign keys)

---

## Database Constraints Summary

### Primary Keys
All tables use auto-incrementing BIGINT primary keys named `id`.

### Foreign Keys
| Child Table        | Foreign Key Column | References      | On Delete Behavior |
|--------------------|--------------------|-----------------|--------------------|
| milk_production    | cow_id             | cows(id)        | CASCADE            |
| health_records     | cow_id             | cows(id)        | CASCADE            |
| breeding_records   | cow_id             | cows(id)        | CASCADE            |

### Unique Constraints
| Table              | Columns                | Purpose                                    |
|--------------------|------------------------|--------------------------------------------|
| users              | username               | Ensure unique login credentials            |
| cows               | tag_id                 | Ensure unique physical identification      |
| milk_production    | (cow_id, date)         | Prevent duplicate daily production records |

### Check Constraints
| Table                  | Column                  | Constraint           | Purpose                              |
|------------------------|-------------------------|----------------------|--------------------------------------|
| milk_production        | morning_quantity        | >= 0                 | Prevent negative production values   |
| milk_production        | evening_quantity        | >= 0                 | Prevent negative production values   |
| milk_production        | total_quantity          | >= 0                 | Prevent negative production values   |
| health_records         | withdrawal_period_days  | >= 0                 | Prevent negative withdrawal periods  |
| health_records         | cost                    | >= 0                 | Prevent negative costs               |
| financial_transactions | amount                  | > 0                  | Ensure positive transaction amounts  |

### Default Values
| Table                  | Column      | Default Value      | Purpose                                    |
|------------------------|-------------|--------------------|---------------------------------------------|
| users                  | active      | TRUE               | New users are active by default             |
| cows                   | status      | 'ACTIVE'           | New cows are active by default              |
| health_records         | withdrawal  | 0                  | No withdrawal period by default             |
| financial_transactions | deleted     | FALSE              | Transactions are not deleted by default     |

---

## Indexes for Performance Optimization

### Table: COWS
- `idx_cow_tag_id` on `tag_id` - Fast lookup by physical tag
- `idx_cow_status` on `status` - Efficient filtering by status (ACTIVE, SOLD, DECEASED)

### Table: MILK_PRODUCTION
- `idx_milk_production_cow_id` on `cow_id` - Fast retrieval of all production records for a cow
- `idx_milk_production_date` on `date` - Efficient date range queries for trends

### Table: HEALTH_RECORDS
- `idx_health_records_cow_id` on `cow_id` - Fast retrieval of health history for a cow
- `idx_health_records_date` on `date` - Efficient date range queries
- `idx_health_records_type` on `record_type` - Fast filtering by record type

### Table: FINANCIAL_TRANSACTIONS
- `idx_transaction_date` on `date` - Efficient date range queries for financial reports
- `idx_transaction_type` on `type` - Fast filtering by INCOME/EXPENSE
- `idx_transaction_category` on `category` - Efficient category-based aggregations

---

## Audit and Versioning

All tables include audit fields for tracking record lifecycle:

- **created_at**: Automatically set when record is created (immutable)
- **updated_at**: Automatically updated on every record modification
- **version**: Optimistic locking version number to prevent lost updates in concurrent scenarios

### Optimistic Locking Strategy
The `version` field implements optimistic locking:
1. When reading a record, the version number is retrieved
2. When updating, the WHERE clause includes the original version number
3. If another transaction modified the record, the version won't match and update fails
4. Application can retry or notify user of conflict

---

**End of Data Dictionary Document**
