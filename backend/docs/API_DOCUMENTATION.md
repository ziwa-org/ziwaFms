# Ziwa Dairy Farm Analytics and Management System - API Documentation

## Overview

This document provides a comprehensive summary of all API endpoints in the Ziwa Dairy Farm Analytics and Management System. The API is built with Spring Boot and follows RESTful principles.

**Base URL:** `http://localhost:8080`

**API Documentation UI:** `http://localhost:8080/swagger-ui.html`

**OpenAPI Specification:** `http://localhost:8080/api-docs`

## Authentication

Most endpoints require JWT authentication. To access protected endpoints:

1. Register a user via `POST /api/auth/register` or login via `POST /api/auth/login`
2. Include the JWT token in the `Authorization` header: `Bearer <token>`

**Public Endpoints (No Authentication Required):**
- `POST /api/auth/register`
- `POST /api/auth/login`

**Protected Endpoints:** All other endpoints require authentication

## API Endpoints

### Authentication Endpoints

#### POST /api/auth/register
Register a new user account.

**Request Body:**
```json
{
  "username": "admin",
  "password": "password123",
  "role": "ADMIN",
  "fullName": "John Doe"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN",
    "fullName": "John Doe"
  }
}
```

**Possible Errors:**
- `400 Bad Request` - Invalid input or username already exists
- `500 Internal Server Error`

---

#### POST /api/auth/login
Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN",
    "fullName": "John Doe"
  }
}
```

**Possible Errors:**
- `401 Unauthorized` - Invalid credentials
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error`

---

#### POST /api/auth/refresh
Refresh JWT token for authenticated user.

**Authentication:** Required

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN",
    "fullName": "John Doe"
  }
}
```

**Possible Errors:**
- `401 Unauthorized` - Invalid or expired token
- `500 Internal Server Error`

---

#### GET /api/auth/me
Get current authenticated user information.

**Authentication:** Required

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin",
  "role": "ADMIN",
  "fullName": "John Doe"
}
```

**Possible Errors:**
- `401 Unauthorized` - Invalid or expired token
- `500 Internal Server Error`

---

### Livestock Management Endpoints

#### POST /api/cows
Register a new cow.

**Authentication:** Required

**Request Body:**
```json
{
  "tagId": "COW-001",
  "breed": "Holstein",
  "dateOfBirth": "2020-01-15",
  "acquisitionDate": "2021-03-20",
  "status": "ACTIVE"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "tagId": "COW-001",
  "breed": "Holstein",
  "dateOfBirth": "2020-01-15",
  "acquisitionDate": "2021-03-20",
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Possible Errors:**
- `400 Bad Request` - Invalid input or duplicate tag ID
- `401 Unauthorized`

---

#### GET /api/cows/{id}
Get cow by ID.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Cow ID

**Response (200 OK):**
```json
{
  "id": 1,
  "tagId": "COW-001",
  "breed": "Holstein",
  "dateOfBirth": "2020-01-15",
  "acquisitionDate": "2021-03-20",
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Possible Errors:**
- `404 Not Found` - Cow not found
- `401 Unauthorized`

---

#### GET /api/cows
List cows with filtering and pagination.

**Authentication:** Required

**Query Parameters:**
- `status` (optional) - Filter by cow status (ACTIVE, SOLD, DECEASED)
- `breed` (optional) - Filter by breed
- `page` (optional) - Page number (0-indexed)
- `size` (optional) - Page size (default: 20)
- `sortBy` (optional) - Sort field
- `sortDirection` (optional) - Sort direction (asc/desc)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "tagId": "COW-001",
      "breed": "Holstein",
      "dateOfBirth": "2020-01-15",
      "acquisitionDate": "2021-03-20",
      "status": "ACTIVE",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

**Possible Errors:**
- `401 Unauthorized`

---

#### PUT /api/cows/{id}
Update cow information.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Cow ID

**Request Body:**
```json
{
  "tagId": "COW-001",
  "breed": "Holstein",
  "dateOfBirth": "2020-01-15",
  "acquisitionDate": "2021-03-20",
  "status": "ACTIVE"
}
```

**Response (200 OK):** Same as cow response

**Possible Errors:**
- `400 Bad Request` - Invalid input
- `404 Not Found` - Cow not found
- `401 Unauthorized`

---

#### DELETE /api/cows/{id}
Delete a cow.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Cow ID

**Response (204 No Content)**

**Possible Errors:**
- `400 Bad Request` - Cow has associated records
- `404 Not Found` - Cow not found
- `401 Unauthorized`

---

#### PATCH /api/cows/{id}/status
Update cow status.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Cow ID

**Query Parameters:**
- `status` (required) - New status (ACTIVE, SOLD, DECEASED)

**Response (200 OK):** Same as cow response

**Possible Errors:**
- `400 Bad Request` - Invalid status
- `404 Not Found` - Cow not found
- `401 Unauthorized`

---

#### POST /api/cows/{id}/breeding
Add breeding record for a cow.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Cow ID

**Request Body:**
```json
{
  "breedingDate": "2024-01-15",
  "bullId": "BULL-001",
  "expectedCalvingDate": "2024-10-15",
  "notes": "First breeding"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "cowId": 1,
  "cowTagId": "COW-001",
  "breedingDate": "2024-01-15",
  "bullId": "BULL-001",
  "expectedCalvingDate": "2024-10-15",
  "actualCalvingDate": null,
  "notes": "First breeding",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Possible Errors:**
- `400 Bad Request` - Invalid input or future breeding date
- `404 Not Found` - Cow not found
- `401 Unauthorized`

---

#### GET /api/cows/{id}/breeding
Get breeding records for a cow.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Cow ID

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "cowId": 1,
    "cowTagId": "COW-001",
    "breedingDate": "2024-01-15",
    "bullId": "BULL-001",
    "expectedCalvingDate": "2024-10-15",
    "actualCalvingDate": null,
    "notes": "First breeding",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

**Possible Errors:**
- `404 Not Found` - Cow not found
- `401 Unauthorized`

---

### Milk Production Endpoints

#### POST /api/production
Record milk production.

**Authentication:** Required

**Request Body:**
```json
{
  "cowId": 1,
  "date": "2024-01-15",
  "morningQuantity": 15.5,
  "eveningQuantity": 14.2,
  "notes": "Normal production"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "cowId": 1,
  "cowTagId": "COW-001",
  "date": "2024-01-15",
  "morningQuantity": 15.5,
  "eveningQuantity": 14.2,
  "totalQuantity": 29.7,
  "notes": "Normal production",
  "createdAt": "2024-01-15T10:30:00"
}
```

**Possible Errors:**
- `400 Bad Request` - Invalid input, negative quantities, or duplicate record
- `404 Not Found` - Cow not found
- `401 Unauthorized`

---

#### GET /api/production
List production records with filtering.

**Authentication:** Required

**Query Parameters:**
- `cowId` (optional) - Filter by cow ID
- `startDate` (optional) - Start date for date range filter
- `endDate` (optional) - End date for date range filter
- `page` (optional) - Page number (0-indexed)
- `size` (optional) - Page size (default: 20)
- `sortBy` (optional) - Sort field
- `sortDirection` (optional) - Sort direction (asc/desc)

**Response (200 OK):** Paginated list of production records

**Possible Errors:**
- `401 Unauthorized`

---

#### GET /api/production/{id}
Get production record by ID.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Production record ID

**Response (200 OK):** Same as production record response

**Possible Errors:**
- `404 Not Found` - Production record not found
- `401 Unauthorized`

---

#### PUT /api/production/{id}
Update production record.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Production record ID

**Request Body:** Same as create production request

**Response (200 OK):** Same as production record response

**Possible Errors:**
- `400 Bad Request` - Invalid input
- `404 Not Found` - Production record not found
- `401 Unauthorized`

---

#### DELETE /api/production/{id}
Delete production record.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Production record ID

**Response (204 No Content)**

**Possible Errors:**
- `404 Not Found` - Production record not found
- `401 Unauthorized`

---

#### GET /api/production/analytics/trends
Get production trends over time.

**Authentication:** Required

**Query Parameters:**
- `startDate` (required) - Start date
- `endDate` (required) - End date

**Response (200 OK):**
```json
[
  {
    "date": "2024-01-15",
    "totalProduction": 150.5,
    "averagePerCow": 15.05,
    "recordCount": 10
  }
]
```

**Possible Errors:**
- `400 Bad Request` - Invalid date range
- `401 Unauthorized`

---

#### GET /api/production/analytics/cow-productivity
Get productivity metrics per cow.

**Authentication:** Required

**Query Parameters:**
- `limit` (optional) - Number of results to return

**Response (200 OK):**
```json
[
  {
    "cowId": 1,
    "cowTagId": "COW-001",
    "averageProduction": 29.7
  }
]
```

**Possible Errors:**
- `401 Unauthorized`

---

#### GET /api/production/analytics/top-producers
Get top producing cows.

**Authentication:** Required

**Query Parameters:**
- `limit` (optional) - Number of top producers to return (default: 10)

**Response (200 OK):**
```json
[
  {
    "cowId": 1,
    "cowTagId": "COW-001",
    "totalProduction": 890.5,
    "averageProduction": 29.7,
    "recordCount": 30
  }
]
```

**Possible Errors:**
- `401 Unauthorized`

---

### Health Management Endpoints

#### POST /api/health
Create health record.

**Authentication:** Required

**Request Body:**
```json
{
  "cowId": 1,
  "date": "2024-01-15",
  "recordType": "VACCINATION",
  "description": "Annual vaccination",
  "veterinarianName": "Dr. Smith",
  "medication": "Vaccine XYZ",
  "withdrawalPeriodDays": 7,
  "cost": 50.00
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "cowId": 1,
  "cowTagId": "COW-001",
  "date": "2024-01-15",
  "recordType": "VACCINATION",
  "description": "Annual vaccination",
  "veterinarianName": "Dr. Smith",
  "medication": "Vaccine XYZ",
  "withdrawalPeriodDays": 7,
  "withdrawalEndDate": "2024-01-22",
  "cost": 50.00,
  "createdAt": "2024-01-15T10:30:00"
}
```

**Possible Errors:**
- `400 Bad Request` - Invalid input
- `404 Not Found` - Cow not found
- `401 Unauthorized`

---

#### GET /api/health
List health records with filtering.

**Authentication:** Required

**Query Parameters:**
- `cowId` (optional) - Filter by cow ID
- `recordType` (optional) - Filter by record type (VACCINATION, TREATMENT, CHECKUP)
- `startDate` (optional) - Start date for date range filter
- `endDate` (optional) - End date for date range filter
- `page` (optional) - Page number (0-indexed)
- `size` (optional) - Page size (default: 20)
- `sortBy` (optional) - Sort field
- `sortDirection` (optional) - Sort direction (asc/desc)

**Response (200 OK):** Paginated list of health records

**Possible Errors:**
- `401 Unauthorized`

---

#### GET /api/health/{id}
Get health record by ID.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Health record ID

**Response (200 OK):** Same as health record response

**Possible Errors:**
- `404 Not Found` - Health record not found
- `401 Unauthorized`

---

#### PUT /api/health/{id}
Update health record.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Health record ID

**Request Body:** Same as create health record request

**Response (200 OK):** Same as health record response

**Possible Errors:**
- `400 Bad Request` - Invalid input
- `404 Not Found` - Health record not found
- `401 Unauthorized`

---

#### DELETE /api/health/{id}
Delete health record.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Health record ID

**Response (204 No Content)**

**Possible Errors:**
- `404 Not Found` - Health record not found
- `401 Unauthorized`

---

#### GET /api/health/withdrawals/active
Get cows currently in withdrawal period.

**Authentication:** Required

**Response (200 OK):**
```json
[
  {
    "cowId": 1,
    "cowTagId": "COW-001",
    "healthRecordId": 1,
    "withdrawalEndDate": "2024-01-22",
    "daysRemaining": 5,
    "medication": "Vaccine XYZ"
  }
]
```

**Possible Errors:**
- `401 Unauthorized`

---

### Financial Management Endpoints

#### POST /api/financial/transactions
Create financial transaction.

**Authentication:** Required

**Request Body:**
```json
{
  "date": "2024-01-15",
  "type": "INCOME",
  "category": "MILK_SALES",
  "amount": 500.00,
  "description": "Milk sales for January 15",
  "referenceId": "INV-001"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "date": "2024-01-15",
  "type": "INCOME",
  "category": "MILK_SALES",
  "amount": 500.00,
  "description": "Milk sales for January 15",
  "referenceId": "INV-001",
  "deleted": false,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Possible Errors:**
- `400 Bad Request` - Invalid input or negative amount
- `401 Unauthorized`

---

#### GET /api/financial/transactions
List financial transactions with filtering.

**Authentication:** Required

**Query Parameters:**
- `type` (optional) - Filter by transaction type (INCOME, EXPENSE)
- `category` (optional) - Filter by category
- `startDate` (optional) - Start date for date range filter
- `endDate` (optional) - End date for date range filter
- `page` (optional) - Page number (0-indexed)
- `size` (optional) - Page size (default: 20)
- `sortBy` (optional) - Sort field
- `sortDirection` (optional) - Sort direction (asc/desc)

**Response (200 OK):** Paginated list of transactions

**Possible Errors:**
- `401 Unauthorized`

---

#### GET /api/financial/transactions/{id}
Get transaction by ID.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Transaction ID

**Response (200 OK):** Same as transaction response

**Possible Errors:**
- `404 Not Found` - Transaction not found
- `401 Unauthorized`

---

#### PUT /api/financial/transactions/{id}
Update transaction.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Transaction ID

**Request Body:** Same as create transaction request

**Response (200 OK):** Same as transaction response

**Possible Errors:**
- `400 Bad Request` - Invalid input
- `404 Not Found` - Transaction not found
- `401 Unauthorized`

---

#### DELETE /api/financial/transactions/{id}
Soft delete transaction.

**Authentication:** Required

**Path Parameters:**
- `id` (Long) - Transaction ID

**Response (204 No Content)**

**Possible Errors:**
- `404 Not Found` - Transaction not found
- `401 Unauthorized`

---

#### GET /api/financial/analytics/profit-loss
Get profit/loss for a date range.

**Authentication:** Required

**Query Parameters:**
- `startDate` (required) - Start date
- `endDate` (required) - End date

**Response (200 OK):**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "totalIncome": 15000.00,
  "totalExpenses": 8000.00,
  "netProfit": 7000.00,
  "profitMargin": 46.67
}
```

**Possible Errors:**
- `400 Bad Request` - Invalid date range
- `401 Unauthorized`

---

#### GET /api/financial/analytics/income-breakdown
Get income breakdown by category.

**Authentication:** Required

**Query Parameters:**
- `startDate` (required) - Start date
- `endDate` (required) - End date

**Response (200 OK):**
```json
[
  {
    "category": "MILK_SALES",
    "total": 12000.00,
    "percentage": 80.0,
    "transactionCount": 30
  },
  {
    "category": "LIVESTOCK_SALES",
    "total": 3000.00,
    "percentage": 20.0,
    "transactionCount": 2
  }
]
```

**Possible Errors:**
- `400 Bad Request` - Invalid date range
- `401 Unauthorized`

---

#### GET /api/financial/analytics/expense-breakdown
Get expense breakdown by category.

**Authentication:** Required

**Query Parameters:**
- `startDate` (required) - Start date
- `endDate` (required) - End date

**Response (200 OK):**
```json
[
  {
    "category": "FEED",
    "total": 5000.00,
    "percentage": 62.5,
    "transactionCount": 15
  },
  {
    "category": "MEDICINE",
    "total": 2000.00,
    "percentage": 25.0,
    "transactionCount": 8
  },
  {
    "category": "LABOR",
    "total": 1000.00,
    "percentage": 12.5,
    "transactionCount": 4
  }
]
```

**Possible Errors:**
- `400 Bad Request` - Invalid date range
- `401 Unauthorized`

---

#### GET /api/financial/analytics/trends
Get monthly financial trends.

**Authentication:** Required

**Query Parameters:**
- `startDate` (required) - Start date
- `endDate` (required) - End date

**Response (200 OK):**
```json
[
  {
    "month": "2024-01",
    "totalIncome": 15000.00,
    "totalExpenses": 8000.00,
    "netProfit": 7000.00
  },
  {
    "month": "2024-02",
    "totalIncome": 16000.00,
    "totalExpenses": 8500.00,
    "netProfit": 7500.00
  }
]
```

**Possible Errors:**
- `400 Bad Request` - Invalid date range
- `401 Unauthorized`

---

### Analytics Endpoints

#### GET /api/analytics/dashboard
Get dashboard summary with key metrics.

**Authentication:** Required

**Response (200 OK):**
```json
{
  "activeCowsCount": 50,
  "todayProduction": 1485.5,
  "monthlyFinancialSummary": {
    "month": "2024-01",
    "totalIncome": 15000.00,
    "totalExpenses": 8000.00,
    "netProfit": 7000.00
  },
  "cowsInWithdrawal": 3,
  "upcomingVaccinations": [
    {
      "cowId": 1,
      "cowTagId": "COW-001",
      "vaccinationDate": "2024-01-20",
      "description": "Annual booster"
    }
  ],
  "productionTrend30Days": [
    {
      "date": "2024-01-15",
      "totalProduction": 1485.5
    }
  ],
  "topProducers": [
    {
      "cowId": 1,
      "cowTagId": "COW-001",
      "totalProduction": 890.5
    }
  ]
}
```

**Possible Errors:**
- `401 Unauthorized`

---

#### GET /api/analytics/production-comparison
Compare production across different time periods.

**Authentication:** Required

**Query Parameters:**
- `startDate1` (required) - Start date of first period
- `endDate1` (required) - End date of first period
- `startDate2` (required) - Start date of second period
- `endDate2` (required) - End date of second period

**Response (200 OK):**
```json
{
  "period1": {
    "startDate": "2024-01-01",
    "endDate": "2024-01-15",
    "totalProduction": 22282.5,
    "averageDaily": 1485.5,
    "recordCount": 750
  },
  "period2": {
    "startDate": "2023-12-01",
    "endDate": "2023-12-15",
    "totalProduction": 20000.0,
    "averageDaily": 1333.3,
    "recordCount": 750
  },
  "comparison": {
    "productionChange": 2282.5,
    "productionChangePercentage": 11.41,
    "averageDailyChange": 152.2,
    "averageDailyChangePercentage": 11.41
  }
}
```

**Possible Errors:**
- `400 Bad Request` - Invalid date ranges
- `401 Unauthorized`

---

## Error Response Format

All error responses follow a consistent structure:

```json
{
  "code": "NOT_FOUND",
  "message": "Cow not found with ID: 123",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Common Error Codes

- `NOT_FOUND` (404) - Resource not found
- `VALIDATION_FAILED` (400) - Request validation failed
- `DUPLICATE_RESOURCE` (409) - Resource already exists
- `UNAUTHORIZED` (401) - Authentication required or invalid token
- `FORBIDDEN` (403) - Insufficient permissions
- `INTERNAL_ERROR` (500) - Internal server error

### Validation Error Response

When validation fails, the response includes detailed field errors:

```json
{
  "code": "VALIDATION_FAILED",
  "message": "Request validation failed",
  "errors": [
    {
      "field": "tagId",
      "message": "Tag ID is required"
    },
    {
      "field": "dateOfBirth",
      "message": "Date of birth cannot be in the future"
    }
  ],
  "timestamp": "2024-01-15T10:30:00"
}
```

## Data Types and Enums

### CowStatus
- `ACTIVE` - Cow is currently active in the farm
- `SOLD` - Cow has been sold
- `DECEASED` - Cow is deceased

### HealthRecordType
- `VACCINATION` - Vaccination record
- `TREATMENT` - Medical treatment
- `CHECKUP` - Routine checkup

### TransactionType
- `INCOME` - Income transaction
- `EXPENSE` - Expense transaction

### UserRole
- `ADMIN` - Full system access
- `MANAGER` - Read/write access to all modules
- `USER` - Read-only access

## Rate Limiting

Authentication endpoints (`/api/auth/**`) are rate-limited to prevent abuse:
- Maximum 5 requests per minute per IP address
- Exceeding the limit returns `429 Too Many Requests`

## Pagination

List endpoints support pagination with the following parameters:
- `page` - Page number (0-indexed, default: 0)
- `size` - Page size (default: 20, max: 100)
- `sortBy` - Field to sort by
- `sortDirection` - Sort direction (`asc` or `desc`)

Paginated responses include:
- `content` - Array of items
- `page` - Current page number
- `size` - Page size
- `totalElements` - Total number of items
- `totalPages` - Total number of pages

## Best Practices

1. **Always include the Authorization header** for protected endpoints
2. **Handle token expiration** - Refresh tokens before they expire or handle 401 errors
3. **Use pagination** for list endpoints to avoid large responses
4. **Validate input** on the client side before sending requests
5. **Handle errors gracefully** - Check error codes and messages
6. **Use appropriate HTTP methods** - GET for reading, POST for creating, PUT for updating, DELETE for deleting
7. **Include meaningful descriptions** in transaction and health records for better tracking

## Support

For API support or questions, contact: support@ziwadairy.com

## Version History

- **v1.0.0** (2024-01-15) - Initial release with all core features
