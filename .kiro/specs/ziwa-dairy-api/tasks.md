# Implementation Plan: Ziwa Dairy Farm Analytics and Management System REST API

## Overview

This implementation plan builds a production-ready Spring Boot REST API with PostgreSQL for comprehensive dairy farm management. The implementation follows a layered architecture (Controller → Service → Repository) and includes JWT authentication, comprehensive validation, property-based testing, and API documentation. The plan builds incrementally, starting with core infrastructure, then implementing each module with its tests, and finally integrating everything with security and documentation.

## Tasks

- [x] 1. Set up project infrastructure and core configuration
  - Configure Spring Boot project with required dependencies (Spring Web, Spring Data JPA, Spring Security, PostgreSQL driver, Bean Validation, JUnit-Quickcheck, Springdoc OpenAPI)
  - Set up application.properties with database connection, JPA settings, and JWT configuration
  - Create base package structure (controller, service, repository, model, dto, exception, config, security)
  - Configure PostgreSQL database schema initialization
  - Set up global exception handler with custom exception classes
  - Create base error response DTOs (ErrorResponse, ValidationErrorResponse, FieldError)
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 13.4_

- [ ] 2. Implement livestock management module
  - [x] 2.1 Create Cow entity and repository
    - Define Cow entity with all fields, relationships, and JPA annotations
    - Create CowRepository interface with custom query methods
    - Add database indexes for tagId and status fields
    - _Requirements: 1.1, 11.1, 11.3_
  
  - [x] 2.2 Create BreedingRecord entity and repository
    - Define BreedingRecord entity with relationship to Cow
    - Create BreedingRecordRepository interface
    - _Requirements: 2.1_
  
  - [x] 2.3 Implement CowService with business logic
    - Implement registerCow with tag ID uniqueness validation
    - Implement getCowById with not found handling
    - Implement listCows with filtering by status and breed
    - Implement updateCow with validation
    - Implement deleteCow with dependency checking
    - Implement updateCowStatus
    - Implement addBreedingRecord with date validation
    - Implement getBreedingRecords
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 2.1, 2.2, 2.3, 2.4_
  
  - [x] 2.4 Create DTOs for cow management
    - Create CowRegistrationRequest with validation annotations
    - Create CowUpdateRequest with validation annotations
    - Create CowResponse DTO
    - Create BreedingRecordRequest with validation annotations
    - Create BreedingRecordResponse DTO
    - _Requirements: 1.1, 2.1_
  
  - [x] 2.5 Implement CowController with REST endpoints
    - POST /api/cows - Register new cow
    - GET /api/cows/{id} - Get cow by ID
    - GET /api/cows - List cows with filtering and pagination
    - PUT /api/cows/{id} - Update cow
    - DELETE /api/cows/{id} - Delete cow
    - PATCH /api/cows/{id}/status - Update cow status
    - POST /api/cows/{id}/breeding - Add breeding record
    - GET /api/cows/{id}/breeding - Get breeding records
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 2.1, 2.2, 2.3, 2.4_
  
  - [ ]* 2.6 Write property test for cow registration round-trip
    - **Property 1: Cow registration round-trip**
    - **Validates: Requirements 1.1, 1.4**
  
  - [ ]* 2.7 Write property test for duplicate tag ID rejection
    - **Property 2: Duplicate tag ID rejection**
    - **Validates: Requirements 1.2**
  
  - [ ]* 2.8 Write property test for status update correctness
    - **Property 3: Status update correctness**
    - **Validates: Requirements 1.3**
  
  - [ ]* 2.9 Write property test for cow filtering correctness
    - **Property 4: Cow filtering correctness**
    - **Validates: Requirements 1.5**
  
  - [ ]* 2.10 Write property test for referential integrity enforcement
    - **Property 5: Referential integrity enforcement**
    - **Validates: Requirements 1.6, 11.2**
  
  - [ ]* 2.11 Write unit tests for cow management edge cases
    - Test empty tag ID rejection
    - Test invalid date handling
    - Test not found scenarios
    - _Requirements: 1.1, 1.2, 1.4_

- [x] 3. Checkpoint - Ensure livestock module tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 4. Implement milk production tracking module
  - [x] 4.1 Create MilkProduction entity and repository
    - Define MilkProduction entity with unique constraint on cow_id + date
    - Create MilkProductionRepository with custom query methods for trends and analytics
    - Add database indexes for cow_id and date fields
    - _Requirements: 3.1, 3.4, 11.1_
  
  - [x] 4.2 Implement MilkProductionService with business logic
    - Implement recordProduction with total calculation and uniqueness validation
    - Implement getProductionById with not found handling
    - Implement listProduction with filtering by cow and date range
    - Implement updateProduction with total recalculation
    - Implement deleteProduction
    - Implement getProductionTrends with daily aggregation
    - Implement getCowProductivity with average calculation
    - Implement getTopProducers with ranking logic
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 4.1, 4.2, 4.3, 4.4_
  
  - [x] 4.3 Create DTOs for production management
    - Create ProductionRecordRequest with validation annotations
    - Create ProductionRecordResponse DTO
    - Create ProductionTrendResponse DTO
    - Create CowProductivityDTO
    - Create TopProducerDTO
    - _Requirements: 3.1, 4.1, 4.2, 4.4_
  
  - [x] 4.4 Implement MilkProductionController with REST endpoints
    - POST /api/production - Record milk production
    - GET /api/production - List production records with filtering
    - GET /api/production/{id} - Get production record by ID
    - PUT /api/production/{id} - Update production record
    - DELETE /api/production/{id} - Delete production record
    - GET /api/production/analytics/trends - Get production trends
    - GET /api/production/analytics/cow-productivity - Get per-cow productivity
    - GET /api/production/analytics/top-producers - Get top producers
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 4.1, 4.2, 4.3, 4.4_
  
  - [ ]* 4.5 Write property test for production record round-trip
    - **Property 10: Production record round-trip**
    - **Validates: Requirements 3.1**
  
  - [ ]* 4.6 Write property test for negative quantity rejection
    - **Property 11: Negative quantity rejection**
    - **Validates: Requirements 3.2**
  
  - [ ]* 4.7 Write property test for total quantity calculation invariant
    - **Property 12: Total quantity calculation invariant**
    - **Validates: Requirements 3.3, 3.6**
  
  - [ ]* 4.8 Write property test for cow-date uniqueness
    - **Property 13: Cow-date uniqueness**
    - **Validates: Requirements 3.4**
  
  - [ ]* 4.9 Write property test for production trend aggregation
    - **Property 15: Production trend aggregation**
    - **Validates: Requirements 4.1, 12.6**
  
  - [ ]* 4.10 Write property test for cow productivity calculation
    - **Property 16: Cow productivity calculation**
    - **Validates: Requirements 4.2**
  
  - [ ]* 4.11 Write unit tests for production edge cases
    - Test zero quantity handling
    - Test date boundary conditions
    - Test empty result sets
    - _Requirements: 3.1, 3.2, 4.1_

- [x] 5. Checkpoint - Ensure production module tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 6. Implement health management module
  - [x] 6.1 Create HealthRecord entity and repository
    - Define HealthRecord entity with relationship to Cow
    - Create HealthRecordRepository with custom query for active withdrawals
    - Add database indexes for cow_id, date, and recordType fields
    - _Requirements: 5.1, 5.2, 11.1_
  
  - [x] 6.2 Implement HealthService with business logic
    - Implement createHealthRecord with withdrawal end date calculation
    - Implement getHealthRecordById with not found handling
    - Implement listHealthRecords with filtering by cow, type, and date range
    - Implement updateHealthRecord with withdrawal recalculation
    - Implement deleteHealthRecord
    - Implement getActiveWithdrawals with date-based filtering
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_
  
  - [x] 6.3 Create DTOs for health management
    - Create HealthRecordRequest with validation annotations
    - Create HealthRecordResponse DTO with calculated withdrawal end date
    - Create WithdrawalResponse DTO with days remaining calculation
    - _Requirements: 5.1, 5.2, 5.4, 5.5_
  
  - [x] 6.4 Implement HealthController with REST endpoints
    - POST /api/health - Create health record
    - GET /api/health - List health records with filtering
    - GET /api/health/{id} - Get health record by ID
    - PUT /api/health/{id} - Update health record
    - DELETE /api/health/{id} - Delete health record
    - GET /api/health/withdrawals/active - Get cows in withdrawal period
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_
  
  - [ ]* 6.5 Write property test for health record round-trip
    - **Property 18: Health record round-trip**
    - **Validates: Requirements 5.1, 5.2**
  
  - [ ]* 6.6 Write property test for withdrawal end date calculation
    - **Property 20: Withdrawal end date calculation**
    - **Validates: Requirements 5.4**
  
  - [ ]* 6.7 Write property test for active withdrawal filtering
    - **Property 21: Active withdrawal filtering**
    - **Validates: Requirements 5.5, 12.4**
  
  - [ ]* 6.8 Write unit tests for health management edge cases
    - Test zero withdrawal period
    - Test past date handling
    - Test different record types
    - _Requirements: 5.1, 5.2, 5.4_

- [x] 7. Checkpoint - Ensure health module tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 8. Implement financial management module
  - [x] 8.1 Create FinancialTransaction entity and repository
    - Define FinancialTransaction entity with soft delete support
    - Create FinancialTransactionRepository with custom queries for aggregations
    - Add database indexes for date, type, and category fields
    - _Requirements: 6.1, 6.5, 11.1_
  
  - [x] 8.2 Implement FinancialService with business logic
    - Implement createTransaction with validation
    - Implement getTransactionById with not found handling
    - Implement listTransactions with filtering by type, category, and date range
    - Implement updateTransaction with validation
    - Implement softDeleteTransaction
    - Implement calculateProfitLoss with income/expense aggregation
    - Implement getIncomeBreakdown with category grouping
    - Implement getExpenseBreakdown with category grouping
    - Implement getFinancialTrends with monthly aggregation
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 7.1, 7.2, 7.3, 7.4_
  
  - [x] 8.3 Create DTOs for financial management
    - Create TransactionRequest with validation annotations
    - Create TransactionResponse DTO
    - Create ProfitLossResponse DTO
    - Create CategoryBreakdownResponse DTO
    - Create MonthlyTrendDTO
    - _Requirements: 6.1, 7.1, 7.2, 7.3, 7.4_
  
  - [x] 8.4 Implement FinancialController with REST endpoints
    - POST /api/financial/transactions - Create transaction
    - GET /api/financial/transactions - List transactions with filtering
    - GET /api/financial/transactions/{id} - Get transaction by ID
    - PUT /api/financial/transactions/{id} - Update transaction
    - DELETE /api/financial/transactions/{id} - Soft delete transaction
    - GET /api/financial/analytics/profit-loss - Get profit/loss
    - GET /api/financial/analytics/income-breakdown - Get income breakdown
    - GET /api/financial/analytics/expense-breakdown - Get expense breakdown
    - GET /api/financial/analytics/trends - Get financial trends
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 7.1, 7.2, 7.3, 7.4_
  
  - [ ]* 8.5 Write property test for transaction round-trip
    - **Property 22: Transaction round-trip**
    - **Validates: Requirements 6.1**
  
  - [ ]* 8.6 Write property test for negative amount rejection
    - **Property 23: Negative amount rejection**
    - **Validates: Requirements 6.2**
  
  - [ ]* 8.7 Write property test for soft delete behavior
    - **Property 26: Soft delete behavior**
    - **Validates: Requirements 6.5**
  
  - [ ]* 8.8 Write property test for profit/loss calculation
    - **Property 27: Profit/loss calculation**
    - **Validates: Requirements 7.1**
  
  - [ ]* 8.9 Write property test for income breakdown aggregation
    - **Property 28: Income breakdown aggregation**
    - **Validates: Requirements 7.2**
  
  - [ ]* 8.10 Write property test for expense breakdown aggregation
    - **Property 29: Expense breakdown aggregation**
    - **Validates: Requirements 7.3**
  
  - [ ]* 8.11 Write unit tests for financial edge cases
    - Test empty date ranges
    - Test single transaction scenarios
    - Test category grouping with various categories
    - _Requirements: 6.1, 7.1, 7.2, 7.3_

- [x] 9. Checkpoint - Ensure financial module tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 10. Implement authentication and authorization
  - [x] 10.1 Create AppUser entity and repository
    - Define AppUser entity with password hash and role
    - Create UserRepository with findByUsername method
    - Add unique constraint on username
    - _Requirements: 8.1, 11.1_
  
  - [x] 10.2 Implement JWT token generation and validation
    - Create JwtTokenProvider utility class
    - Implement token generation with user claims
    - Implement token validation and parsing
    - Configure token expiration and secret key
    - _Requirements: 8.2, 8.5_
  
  - [x] 10.3 Implement UserService with authentication logic
    - Implement registerUser with password hashing (BCrypt)
    - Implement authenticateUser with credential validation
    - Implement loadUserByUsername for Spring Security
    - _Requirements: 8.1, 8.2, 8.3, 14.5_
  
  - [x] 10.4 Configure Spring Security
    - Create SecurityConfig class with JWT filter
    - Configure authentication entry point
    - Configure authorization rules for endpoints
    - Implement role-based access control
    - Disable CSRF for stateless API
    - _Requirements: 8.4, 8.5, 8.6_
  
  - [x] 10.5 Create DTOs for authentication
    - Create RegisterRequest with validation annotations
    - Create LoginRequest with validation annotations
    - Create AuthResponse DTO with token and user info
    - Create UserInfo DTO
    - _Requirements: 8.1, 8.2_
  
  - [x] 10.6 Implement AuthController with REST endpoints
    - POST /api/auth/register - Register new user
    - POST /api/auth/login - Login and get JWT token
    - POST /api/auth/refresh - Refresh JWT token
    - GET /api/auth/me - Get current user info
    - _Requirements: 8.1, 8.2, 8.5_
  
  - [ ]* 10.7 Write property test for user registration with password hashing
    - **Property 31: User registration with password hashing**
    - **Validates: Requirements 8.1, 14.5**
  
  - [ ]* 10.8 Write property test for valid credentials authentication
    - **Property 32: Valid credentials authentication**
    - **Validates: Requirements 8.2**
  
  - [ ]* 10.9 Write property test for invalid credentials rejection
    - **Property 33: Invalid credentials rejection**
    - **Validates: Requirements 8.3**
  
  - [ ]* 10.10 Write property test for unauthorized request rejection
    - **Property 34: Unauthorized request rejection**
    - **Validates: Requirements 8.4, 9.3**
  
  - [ ]* 10.11 Write property test for role-based access control
    - **Property 36: Role-based access control**
    - **Validates: Requirements 8.6, 9.4**
  
  - [ ]* 10.12 Write unit tests for authentication edge cases
    - Test duplicate username rejection
    - Test weak password rejection
    - Test expired token handling
    - Test malformed token handling
    - _Requirements: 8.1, 8.2, 8.3, 8.4_

- [x] 11. Checkpoint - Ensure authentication tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 12. Implement analytics and dashboard module
  - [x] 12.1 Implement AnalyticsService with cross-module logic
    - Implement getDashboardData aggregating from all modules
    - Implement compareProductionPeriods for period comparison
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6, 4.3_
  
  - [x] 12.2 Create DTOs for analytics
    - Create DashboardResponse DTO with all dashboard metrics
    - Create MonthlyFinancialSummary DTO
    - Create UpcomingVaccination DTO
    - Create DailyProduction DTO
    - Create TopProducer DTO
    - Create ProductionComparisonResponse DTO
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6_
  
  - [x] 12.3 Implement AnalyticsController with REST endpoints
    - GET /api/analytics/dashboard - Get dashboard summary
    - GET /api/analytics/production-comparison - Compare production periods
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6, 4.3_
  
  - [ ]* 12.4 Write property test for active cows count accuracy
    - **Property 40: Active cows count accuracy**
    - **Validates: Requirements 12.1**
  
  - [ ]* 12.5 Write property test for today's production total
    - **Property 41: Today's production total**
    - **Validates: Requirements 12.2**
  
  - [ ]* 12.6 Write property test for monthly financial summary
    - **Property 42: Monthly financial summary**
    - **Validates: Requirements 12.3**
  
  - [ ]* 12.7 Write unit tests for dashboard edge cases
    - Test dashboard with no data
    - Test dashboard with partial data
    - Test date boundary conditions
    - _Requirements: 12.1, 12.2, 12.3_

- [ ] 13. Implement pagination and filtering support
  - [x] 13.1 Create pagination utilities and DTOs
    - Create PagedResponse wrapper DTO
    - Create PageRequest utility for building Pageable
    - Create SortRequest utility for building Sort
    - _Requirements: 15.1, 15.2, 15.4_
  
  - [x] 13.2 Update all list endpoints to support pagination
    - Add pagination parameters to all list endpoints
    - Return PagedResponse with total count
    - Add sorting support to all list endpoints
    - _Requirements: 15.1, 15.2, 15.3, 15.4, 15.5_
  
  - [ ]* 13.3 Write property test for pagination correctness
    - **Property 47: Pagination correctness**
    - **Validates: Requirements 15.1, 15.2**
  
  - [ ]* 13.4 Write property test for filter-then-paginate order
    - **Property 48: Filter-then-paginate order**
    - **Validates: Requirements 15.3**
  
  - [ ]* 13.5 Write property test for sorting correctness
    - **Property 49: Sorting correctness**
    - **Validates: Requirements 15.4**
  
  - [ ]* 13.6 Write unit tests for pagination edge cases
    - Test empty page
    - Test single page
    - Test last page with partial results
    - Test default page size
    - _Requirements: 15.1, 15.2, 15.5_

- [ ] 14. Implement input validation and security features
  - [x] 14.1 Create custom validators
    - Create UniqueCowTagValidator
    - Create UniqueProductionRecordValidator
    - Create UniqueUsernameValidator
    - Create DateNotFutureValidator
    - _Requirements: 1.2, 2.2, 3.4, 8.1_
  
  - [x] 14.2 Implement input sanitization
    - Create XSS sanitization utility
    - Apply sanitization to all text inputs
    - _Requirements: 14.2_
  
  - [x] 14.3 Implement rate limiting for authentication endpoints
    - Create rate limiting filter using Bucket4j or similar
    - Apply to /api/auth/** endpoints
    - Configure limits (e.g., 5 requests per minute)
    - _Requirements: 14.4_
  
  - [ ]* 14.4 Write property test for input sanitization
    - **Property 39: Input sanitization**
    - **Validates: Requirements 14.2**
  
  - [ ]* 14.5 Write property test for rate limiting
    - **Property 46: Rate limiting on authentication**
    - **Validates: Requirements 14.4**
  
  - [ ]* 14.6 Write unit tests for validation edge cases
    - Test various XSS patterns
    - Test SQL injection patterns
    - Test rate limit threshold
    - _Requirements: 14.2, 14.4_

- [ ] 15. Implement transaction management and concurrency control
  - [x] 15.1 Add @Transactional annotations to service methods
    - Mark all write operations as transactional
    - Configure rollback rules for exceptions
    - _Requirements: 13.1, 13.2_
  
  - [x] 15.2 Implement optimistic locking
    - Add @Version field to all entities
    - Configure optimistic locking exception handling
    - _Requirements: 13.3_
  
  - [ ]* 15.3 Write property test for transaction atomicity
    - **Property 44: Transaction atomicity**
    - **Validates: Requirements 13.2**
  
  - [ ]* 15.4 Write property test for optimistic locking
    - **Property 45: Optimistic locking for concurrent updates**
    - **Validates: Requirements 13.3**
  
  - [ ]* 15.5 Write integration tests for transaction scenarios
    - Test rollback on exception
    - Test concurrent update conflicts
    - Test cascade operations
    - _Requirements: 13.2, 13.3, 11.5_

- [ ] 16. Configure OpenAPI/Swagger documentation
  - [x] 16.1 Add Springdoc OpenAPI dependency and configuration
    - Configure OpenAPI info (title, version, description)
    - Configure security scheme for JWT
    - Add API documentation annotations to controllers
    - _Requirements: 10.1, 10.2, 10.3_
  
  - [x] 16.2 Add detailed API documentation annotations
    - Add @Operation annotations to all endpoints
    - Add @ApiResponse annotations for all status codes
    - Add @Schema annotations to all DTOs
    - Add example values to request/response schemas
    - _Requirements: 10.2, 10.3, 10.4_
  
  - [x] 16.3 Generate API documentation summary file
    - Create script or utility to extract all endpoints
    - Generate markdown file with routes, methods, and schemas
    - Include authentication requirements
    - Include example requests/responses
    - Save to docs/API_DOCUMENTATION.md
    - _Requirements: 10.5_

- [ ] 17. Write integration tests for end-to-end flows
  - [ ]* 17.1 Write integration test for complete cow lifecycle
    - Register cow → Record production → Add health record → Update status
    - _Requirements: 1.1, 3.1, 5.1, 1.3_
  
  - [ ]* 17.2 Write integration test for financial tracking flow
    - Create income transaction → Create expense transaction → Calculate profit/loss
    - _Requirements: 6.1, 7.1_
  
  - [ ]* 17.3 Write integration test for authentication flow
    - Register user → Login → Access protected endpoint → Refresh token
    - _Requirements: 8.1, 8.2, 8.4_
  
  - [ ]* 17.4 Write integration test for dashboard data aggregation
    - Create test data across all modules → Fetch dashboard → Verify all metrics
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6_

- [x] 18. Final checkpoint and documentation
  - Ensure all tests pass (unit, property, integration)
  - Verify API documentation is complete and accessible
  - Verify all endpoints are secured appropriately
  - Verify database migrations are working
  - Ask the user if questions arise or if any adjustments are needed

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation after each major module
- Property tests validate universal correctness properties with minimum 100 iterations
- Unit tests validate specific examples and edge cases
- Integration tests validate end-to-end flows across modules
- The implementation builds incrementally: infrastructure → modules → security → documentation
- All code should follow Spring Boot best practices and RESTful API design principles
